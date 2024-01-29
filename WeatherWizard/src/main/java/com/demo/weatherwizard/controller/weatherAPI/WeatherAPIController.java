package com.demo.weatherwizard.controller.weatherAPI;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import com.demo.weatherwizard.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RequestMapping(value = "/v1/weatherAPI")
@RestController
public class WeatherAPIController {
    private final APIService apiService;

    @Autowired
    public WeatherAPIController(
            @Qualifier("WeatherAPIServiceImpl") APIService apiService
    ){
        this.apiService = apiService;
    }

//    @GetMapping(value = "/testKafka")
//    public ResponseEntity<String> sendFakeRequestToTestKafka(@RequestBody String json) throws EmptyResponseException, NotValidRequestException, IOException {
//        Optional<String> response = apiService.sendFakeJsonToTestKafka(json);
//        if(response.isEmpty()){
//            return ResponseEntity.internalServerError().body("Internal Server Error, pls check the logs");
//        }
//        return ResponseEntity.ok().body("Request Sended successfully!");
//    }
}
