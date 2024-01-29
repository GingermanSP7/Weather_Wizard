package com.demo.weatherwizard.service.openweatherAPI;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface OpenWeatherAPIService {
    String sendRequestLocation(String city) throws NotValidRequestException, EmptyResponseException, IOException;
}
