package com.sp.sparkstreamingmodule.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.ml.linalg.VectorUDT;

import java.util.Objects;

import static org.apache.spark.sql.functions.col;

@Slf4j
public class LinearRegressionManager {
    private LinearRegression regression = null;
    @Getter
    private LinearRegressionModel linearRegressionModel;


    private Dataset<Row> createFeaturesColumn(Dataset<Row> data) {
        // Lista delle colonne delle feature
        String[] featureColumns = {"feltTemp"};

        // Assembla le colonne delle feature in una colonna "features"
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(featureColumns)
                .setOutputCol("features");

        return assembler.transform(data);
    }

    public Dataset<Row> trainRegression(Dataset<Row> data){
        try{
            log.info("Start training regression model");
            data = createFeaturesColumn(data);
            if(Objects.isNull(regression)) {
                regression = new LinearRegression()
                        .setLabelCol("temp")
                        .setFeaturesCol("features");
            }
//            Column featuresColumn = createFeaturesColumn(data);
//            Column labelFeatures = new Column("temp").plus(featuresColumn).as("labelFeatures");

            linearRegressionModel = regression.fit(data);

            // Restituisci la colonna delle predizioni
            return linearRegressionModel.transform(data);
        }catch (Exception e){
            log.error("Error during applying regression: {}", e.getMessage());
        }
        log.info("End training regression model");
        return null;
    }
}
