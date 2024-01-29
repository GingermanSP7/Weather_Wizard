package com.demo.kafkastreammodule.service;

import com.demo.kafkastreammodule.dto.DataDTO;
import com.demo.kafkastreammodule.kafka.Producer;
import com.demo.kafkastreammodule.model.tomorrowAPI.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService{
    private final Environment env;
    private final StreamsBuilder streamsBuilder;
    private final KafkaProducer<String, String> kafkaProducer;
    private final ObjectMapper mapper;
    private final Gson gson;

    private final DateTimeFormatter dateFormatter;

    @Autowired
    public KafkaServiceImpl(
            Environment env,
            StreamsBuilder streamsBuilder,
            Gson gson
    ){
        this.env = env;
        this.streamsBuilder = streamsBuilder;
        this.kafkaProducer = Producer.getProducer();
        this.mapper = new ObjectMapper();
        this.gson = gson;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX");
    }

    private void sendToTopic(Object data, String topic){
        log.info("Sending message from Producer..");
        try{
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    Objects.requireNonNull(topic),
                    gson.toJson(data)
            );
            log.info("Data JSON sent: {}", gson.toJson(data));
            kafkaProducer.send(record);
            log.info("Message sent to topic: {}", record.topic());
        }catch(Exception e){
            log.error("Error during send message to kafkaTopic: ", e);
        }
    }

    private void dataProcessor(KStream<String, String> stream, String topic){
        DataDTO dataDTOTomorrow = new DataDTO();
        DataDTO dataDTOWeather = new DataDTO();
        DataDTO dataDTOOpenWeather = new DataDTO();

        // TOMORROW API
        stream.filter((key, value) -> value.contains("data"))
                .mapValues(value -> {
                    try {
                        return mapper.readValue(value, Model.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error during mapping with tomorrowAPI object: {}", e.getMessage());
                    }
                    return null;
                })
                .peek((key, value) -> System.out.println("TomorrowAPI map: " + value))
                .mapValues(value -> {
                    dataDTOTomorrow.setTemp(value.getData().getValues().getTemperature());
                    dataDTOTomorrow.setFeltTemp(value.getData().getValues().getTemperatureApparent());
                    dataDTOTomorrow.setHumidity(Double.valueOf(value.getData().getValues().getHumidity()));
                    dataDTOTomorrow.setPrecipProbability(value.getData().getValues().getPrecipitationProbability());
                    dataDTOTomorrow.setRainIntensity(Double.valueOf(value.getData().getValues().getRainIntensity()));
                    dataDTOTomorrow.setWindSpeed(value.getData().getValues().getWindSpeed());
                    dataDTOTomorrow.setPressure(value.getData().getValues().getPressureSurfaceLevel());
                    dataDTOTomorrow.setTimestamp(LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_DATE_TIME).toString());
                    dataDTOTomorrow.setLatitude(String.valueOf(value.getLocation().getLat()));
                    dataDTOTomorrow.setLongitude(String.valueOf(value.getLocation().getLon()));
                    dataDTOTomorrow.setCity(value.getLocation().getName());
                    return dataDTOTomorrow;
                })
                .foreach((key, value) -> sendToTopic(value,topic));

        // WEATHER API
        stream.filter((key, value) -> value.contains("air_quality"))
                .mapValues(value -> {
                    try {
                        return mapper.readValue(value, com.demo.kafkastreammodule.model.weatherAPI.Model.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error during mapping with weatherAPI object: {}", e.getMessage());
                    }
                    return null;
                })
                .peek((key, value) -> System.out.println("WeatherAPI map: " + value))
                .mapValues(value -> {
                    dataDTOWeather.setTemp(value.getCurrent().getTemp_c());
                    dataDTOWeather.setFeltTemp(value.getCurrent().getFeelslike_c());
                    dataDTOWeather.setHumidity(value.getCurrent().getHumidity());
                    dataDTOWeather.setRainIntensity(value.getCurrent().getPrecip_mm());
                    dataDTOWeather.setWindSpeed(value.getCurrent().getWind_kph());
                    dataDTOWeather.setPressure(value.getCurrent().getPressure_mb());
                    dataDTOWeather.setCo(value.getCurrent().getAir_quality().getCo());
                    dataDTOWeather.setNo2(value.getCurrent().getAir_quality().getNo2());
                    dataDTOWeather.setO3(value.getCurrent().getAir_quality().getO3());
                    dataDTOWeather.setSo2(value.getCurrent().getAir_quality().getSo2());
                    dataDTOWeather.setPm2_5(value.getCurrent().getAir_quality().getPm2_5());
                    dataDTOWeather.setPm10(value.getCurrent().getAir_quality().getPm10());
                    dataDTOWeather.setGbDefraIndex(value.getCurrent().getAir_quality().getGbDefraIndex());
                    dataDTOWeather.setUsEpaIndex(value.getCurrent().getAir_quality().getUsEpaIndex());
                    dataDTOWeather.setTimestamp(LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_DATE_TIME).toString());
                    dataDTOWeather.setLatitude(String.valueOf(value.getLocation().getLat()));
                    dataDTOWeather.setLongitude(String.valueOf(value.getLocation().getLon()));
                    dataDTOWeather.setCity(value.getLocation().getName());
                    return dataDTOWeather;
                })
                .foreach((key, value) -> sendToTopic(value,topic));

        // OPENWEATHER API
        stream.filter((key, value) -> value.contains("temp_min"))
                .mapValues(value -> {
                    try {
                        return mapper.readValue(value, com.demo.kafkastreammodule.model.openWeatherAPI.Model.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error during mapping with openWeatherAPI object: {}", e.getMessage());
                    }
                    return null;
                })
                .peek((key, value) -> System.out.println("OpenWeatherAPI map: " + value))
                .mapValues(value -> {
                    dataDTOOpenWeather.setTemp(value.getMain().getTemp());
                    dataDTOOpenWeather.setFeltTemp(value.getMain().getFeels_like());
                    dataDTOOpenWeather.setMinTemp(value.getMain().getTemp_min());
                    dataDTOOpenWeather.setMaxTemp(value.getMain().getTemp_max());
                    dataDTOOpenWeather.setHumidity(Double.valueOf(value.getMain().getHumidity()));
                    dataDTOOpenWeather.setWindSpeed(value.getWind().getSpeed());
                    dataDTOOpenWeather.setPressure(Double.valueOf(value.getMain().getPressure()));
                    dataDTOOpenWeather.setSunset(LocalDateTime.ofInstant(Instant.ofEpochSecond(value.getSys().getSunset()), ZoneOffset.UTC).toString());
                    dataDTOOpenWeather.setSunrise(LocalDateTime.ofInstant(Instant.ofEpochSecond(value.getSys().getSunrise()), ZoneOffset.UTC).toString());
                    dataDTOOpenWeather.setTimestamp(LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_DATE_TIME).toString());
                    dataDTOOpenWeather.setLatitude(String.valueOf(value.getCoord().getLat()));
                    dataDTOOpenWeather.setLongitude(String.valueOf(value.getCoord().getLon()));
                    dataDTOOpenWeather.setCity(value.getName());
                    return dataDTOOpenWeather;
                })
                .foreach((key, value) -> sendToTopic(value,topic));
    }

    @PostConstruct
    @Override
    public void streamTopologyCT() {
        KStream<String, String> stream = streamsBuilder
                .stream(env.getProperty("spring.kafka.topic.ct"), Consumed.with(Serdes.String(), Serdes.String()));

        CompletableFuture.runAsync(() -> dataProcessor(stream, env.getProperty("spring.kafka.producer.topic.temperature.ct")));
    }

    @PostConstruct
    @Override
    public void streamTopologyMI() {
        KStream<String, String> stream = streamsBuilder
                .stream(env.getProperty("spring.kafka.topic.mi"), Consumed.with(Serdes.String(), Serdes.String()));

        CompletableFuture.runAsync(() -> dataProcessor(stream, env.getProperty("spring.kafka.producer.topic.temperature.mi")));
    }

    @PostConstruct
    @Override
    public void streamTopologyRM() {
        KStream<String, String> stream = streamsBuilder
                .stream(env.getProperty("spring.kafka.topic.rm"), Consumed.with(Serdes.String(), Serdes.String()));

        CompletableFuture.runAsync(() -> dataProcessor(stream, env.getProperty("spring.kafka.producer.topic.temperature.rm")));
    }
}
