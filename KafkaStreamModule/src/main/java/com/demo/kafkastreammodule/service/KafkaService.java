package com.demo.kafkastreammodule.service;

import org.springframework.stereotype.Service;

@Service
public interface KafkaService {
    void streamTopologyCT();
    void streamTopologyMI();
    void streamTopologyRM();
}
