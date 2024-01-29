package com.demo.weatherwizard.service;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public interface APIService {
    void initPolling() throws NotValidRequestException, EmptyResponseException, IOException;
    Optional<String> sendFakeJsonToTestKafka(String json);
}
