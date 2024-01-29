package com.demo.kafkastreammodule.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Properties;

@Component
public class Producer {
    private static KafkaProducer<String, String> kafkaProducer;
    private static Properties props;

    @Autowired
    private Producer(
            @Qualifier("producerConfig") Properties props
    ){
        Producer.props = props;
    }

    @Bean
    public static KafkaProducer<String, String> getProducer(){
        if(Objects.isNull(kafkaProducer)){
            return new KafkaProducer<>(props);
        }
        return kafkaProducer;
    }
}
