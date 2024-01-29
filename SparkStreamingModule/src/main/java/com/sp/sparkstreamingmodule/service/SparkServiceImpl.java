package com.sp.sparkstreamingmodule.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.sp.sparkstreamingmodule.manager.LinearRegressionManager;
import com.sp.sparkstreamingmodule.mapping.ElasticMapping;
import com.sp.sparkstreamingmodule.mapping.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.elasticsearch.client.RestClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

import static org.apache.spark.sql.functions.*;

@Service
@Slf4j
public class SparkServiceImpl {
    private final SparkSession session;
    private ElasticsearchClient elasticsearchClient;
    private final StructType schema;
    private static boolean status;
    private final LinearRegressionManager linearRegressionManager;

    private RestClient client;

    public SparkServiceImpl(){
        this.session = getSparkSession();
        this.schema = Schema.schema;
        this.linearRegressionManager = new LinearRegressionManager();
        initializeElasticsearch();
        createIndexes();
    }

    private void initializeElasticsearch(){
        log.info("Creating client for Elasticsearch");
        if(Objects.isNull(this.client)) {
            this.client = RestClient.builder(HttpHost.create("http://20.0.0.9:9200")).build();
            ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
            this.elasticsearchClient = new ElasticsearchClient(transport);
            log.info("Client created successfully!");
        }
        log.info("Client Already created!");
    }

    private void createIndexes(){
        log.info("Start createIndexes method");
        String elastic_mapping = ElasticMapping.elasticMapping;
        ElasticsearchIndicesClient indices = elasticsearchClient.indices();
        try{
            log.info("Checking if exist index..");
            BooleanResponse response = indices.exists(s -> s.index("weather_wizard"));

            if(!response.value()){
                log.info("Index does not exist, creating new index..");
                CreateIndexRequest createIndexRequest = CreateIndexRequest.of(i -> i.index("weather_wizard"));
                CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);

                if(createIndexResponse.acknowledged()){
                    PutMappingRequest.Builder putMappingRequestBuilder = new PutMappingRequest.Builder();
                    putMappingRequestBuilder.index("weather_wizard");
                    putMappingRequestBuilder.withJson(new StringReader(elastic_mapping));
                    PutMappingRequest putMappingRequest = putMappingRequestBuilder.build();
                    PutMappingResponse putMappingResponse = elasticsearchClient.indices().putMapping(putMappingRequest);
                    if(putMappingResponse.acknowledged()){
                        log.info("Index created successfully!");
                    }
                }else{
                    log.warn("Index does not created!");
                }
            }
        }catch (IOException e){
            log.error("Error during create index: {}", e.getMessage());
        }
        log.info("End createIndexes method");
    }

//    private void clearCsv(){
//        try{
//            log.info("Clearing Csv file..");
//            Dataset<Row> csvData = session.read()
//                    .option("header", "true")
//                    .csv("/training/");
//            csvData = csvData.filter("1 == 0");
//            csvData.write().mode("overwrite").csv("/training/");
//            log.info("Csv cleared successfully!");
//        }catch (Exception e){
//            log.error("Exception: {}", e.getMessage());
//        }
//    }

    private void writeToElasticsearch(Dataset<Row> data) {
        try {
            data = data.drop("features");

            // Scrivi i dati in Elasticsearch
            data.write()
                    .mode(SaveMode.Append)
                    .format("org.elasticsearch.spark.sql")
                    .option("checkpointLocation", "/tmp")
                    .option("es.nodes", "http://20.0.0.9")
                    .option("es.port", "9200")
                    .save("weather_wizard");

            log.info("Data written to Elasticsearch index: {}", "weather_wizard");
        } catch (Exception e) {
            log.error("Error writing to Elasticsearch: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 30000)
    public void writeCsv(){
        if(SparkServiceImpl.status) {
            log.info("Start writeCsv method");
            String[] topics = {"WeatherCT", "WeatherRM", "WeatherMI"};
            try {
                Dataset<Row> ds = session.read()
                        .format("kafka")
                        .option("kafka.bootstrap.servers", "20.0.0.5:9092")
                        .option("subscribe", String.join(",", topics))
                        .option("startingOffset", "earliest")
                        .load();

                if (!ds.isEmpty()) {
                    ds = ds.withColumn("timestamp", date_format(ds.col("timestamp"), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX"))
                            .selectExpr("CAST(value as STRING)")
                            .select(from_json(col("value").cast("string"), schema).alias("data"))
                            .selectExpr("data.*");

                    ds = ds.withColumn("latitude", ds.col("latitude").cast("double"))
                            .withColumn("longitude", ds.col("longitude").cast("double"))
                            .withColumn("location", expr("concat(latitude,',',longitude)"));

                    ds.coalesce(1)
                            .write()
                            .option("header", "true")
                            .mode("Overwrite")
                            .csv("/training/");
                }
            } catch (Exception e) {
                log.error("Generic error: {}", e.getMessage());
            }
            log.info("End writeCsv method");
        }
    }

    @Scheduled(fixedRate = 35000)
    public void readCsv(){
        if(SparkServiceImpl.status) {
            log.info("Start readCsv method");
            try {
                Dataset<Row> ds = session.read()
                        .option("header", "true")
                        .schema(schema)
                        .csv("/training/");

                if (!ds.isEmpty()) {
                    ds = this.linearRegressionManager.trainRegression(ds);
                    ds.show();
                    //clearCsv();

                    writeToElasticsearch(ds);
                }
            } catch (Exception e) {
                log.error("Generic error: {}", e.getMessage());
            }
            log.info("End readCsv method");
        }
    }

    public Boolean startSpark() {
        if(!SparkServiceImpl.status){
            SparkServiceImpl.status = true;
            log.info("Spark Started!");
            return true;
        }
        log.debug("Spark is already running");
        return false;
    }

    public Boolean stopSpark() {
        if(SparkServiceImpl.status){
            SparkServiceImpl.status = false;
            try {
                this.client.close();
            }catch (IOException e){
                log.error("Error during close Elastic client: {}", e.getMessage());
            }
            log.info("Spark Stopped!");
            return true;
        }
        log.debug("Spark is already stopped");
        return false;
    }
    public SparkSession getSparkSession(){
        log.info("pathname spark-warehouse: {}", "${user.dir}/spark-warehouse");
        if(Objects.isNull(session)){
            return SparkSession.builder()
                    .appName("WeatherWizard")
                    .master("local[3]")
                    .config("spark.sql.warehouse.dir", "${user.dir}/spark-warehouse")
                    .config("spark.es.nodes","http://20.0.0.9:9200")
                    .getOrCreate();
        }
       return this.session;
    }
}
