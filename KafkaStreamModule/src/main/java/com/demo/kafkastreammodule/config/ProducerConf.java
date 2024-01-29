package com.demo.kafkastreammodule.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class ProducerConf {
    private final Environment env;
    private Properties props;

    @Autowired
    public ProducerConf(
            Environment env
    ){
        this.env = env;
    }

    @Bean
    public Properties producerConfig(){
        this.props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.producer.bootstrap-servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_DOC, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_DOC, StringSerializer.class);
        return props;
    }
}
