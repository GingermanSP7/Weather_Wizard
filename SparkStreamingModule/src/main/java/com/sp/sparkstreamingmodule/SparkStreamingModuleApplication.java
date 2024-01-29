package com.sp.sparkstreamingmodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.SparkConf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SparkStreamingModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparkStreamingModuleApplication.class, args);
    }

    @Bean
    public ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
