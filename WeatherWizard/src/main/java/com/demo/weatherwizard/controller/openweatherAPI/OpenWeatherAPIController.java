package com.demo.weatherwizard.controller.openweatherAPI;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import com.demo.weatherwizard.service.APIService;
import com.demo.weatherwizard.service.openweatherAPI.OpenWeatherAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@RequestMapping(value = "/v3/openwatherAPI")
@RestController
public class OpenWeatherAPIController {
    private final OpenWeatherAPIService openWeatherAPIService;
    private final APIService apiService;

    @Autowired
    public OpenWeatherAPIController(
            OpenWeatherAPIService openWeatherAPIService,
            @Qualifier("OpenWeatherAPIServiceImpl") APIService apiService
    ){
        this.openWeatherAPIService = openWeatherAPIService;
        this.apiService = apiService;
    }
    @GetMapping(value = "/getInfo")
    public ResponseEntity<String> getCoordinatesLocation() throws IOException, EmptyResponseException, NotValidRequestException {
        String geoLocationCt = openWeatherAPIService.sendRequestLocation("Catania");
        String geoLocationMi = openWeatherAPIService.sendRequestLocation("Milano");
        String geoLocationRm = openWeatherAPIService.sendRequestLocation("Roma");

        if(Objects.isNull(geoLocationCt) || geoLocationCt.trim().isEmpty() ){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(geoLocationCt+geoLocationMi+geoLocationRm);
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
