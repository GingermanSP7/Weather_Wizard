package com.demo.weatherwizard.service.openweatherAPI;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import com.demo.weatherwizard.model.openWeatherAPI.Model;
import com.demo.weatherwizard.service.APIService;
import com.demo.weatherwizard.service.polling.PollingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service("OpenWeatherAPIServiceImpl")
@Slf4j
public class OpenWeatherAPIServiceImpl implements APIService, OpenWeatherAPIService {
    private final Environment env;
    private final RestTemplate restTemplate;

    private void sendToFluentd(Model json, String topic) throws NotValidRequestException {
        if(Objects.isNull(json)){
            throw new NotValidRequestException("Not Valid Json to send to Fluentd");
        }
        log.info("Starting sendToFluentd method");

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Model> requestEntity = new HttpEntity<>(json, headers);

            restTemplate.exchange(
                    env.getProperty("web.request.fluentd.url")+topic,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.info("Ended sendToFluentd method for OpenWeatherAPI response");
        }catch (NullPointerException e){
            log.error("NullPointerException: {}", e.getMessage());
        } catch (RestClientException e){
            log.error("RestClientException: {}", e.getMessage());
        }
    }

    private Model sendHttpReq(String city) throws NotValidRequestException, EmptyResponseException {
        String baseUrl = env.getProperty("web.request.openweatherapi.url");
        String token = env.getProperty("web.request.openweatherapi.token");

        String latitude = "";
        String longitude = "";

        switch (city.toLowerCase()){
            case "catania":
                latitude = env.getProperty("web.request.geo.catania.lat");
                longitude = env.getProperty("web.request.geo.catania.lon");
            break;
            case "milano":
                latitude = env.getProperty("web.request.geo.milano.lat");
                longitude = env.getProperty("web.request.geo.milano.lon");
            break;
            case "roma":
                latitude = env.getProperty("web.request.geo.roma.lat");
                longitude = env.getProperty("web.request.geo.roma.lon");
            break;
        }

        log.debug("URL for API request: {}", baseUrl);
        if(Objects.isNull(baseUrl) || baseUrl.trim().isEmpty() || Objects.requireNonNull(latitude).trim().isEmpty() || Objects.requireNonNull(longitude).trim().isEmpty()){
            throw new NotValidRequestException("Mandatory Field not present");
        }
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", token)
                .queryParam("units",env.getProperty("web.request.openweatherapi.units"))
                .queryParam("lang",env.getProperty("web.request.openweatherapi.lang"))
                .build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Model response = mapper.readValue(restTemplate.getForObject(uriComponents.toUri(), String.class), Model.class);
            if (Objects.isNull(response)) {
                throw new EmptyResponseException("Error in response from OpenWeatherAPI");
            }
            return response;
        }catch (HttpClientErrorException e){
            log.error("Error during HTTP response from OpenWeatherAPI {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Exception during mapping response from OpenWeatherAPI: {}", e.getMessage());
        }
        return null;
    }


    @Autowired
    public OpenWeatherAPIServiceImpl(
            Environment env,
            RestTemplate restTemplate
    ){
        this.env = env;
        this.restTemplate = restTemplate;
    }

   @Override
    public String sendRequestLocation(String city) throws NotValidRequestException, EmptyResponseException {
       String baseUrl = env.getProperty("web.request.openweatherapi.geo.url");
       String token = env.getProperty("web.request.openweatherapi.token");

       log.info("URL for GeolocationAPI request: {}", baseUrl);
       if (Objects.isNull(baseUrl) || baseUrl.trim().isEmpty()) {
           throw new NotValidRequestException("Url not present");
       }

       UriComponents uriGeolocation = UriComponentsBuilder.fromUriString(baseUrl)
               .queryParam("q", city)
               .queryParam("limit", env.getProperty("web.request.openweatherapi.geo.limit"))
               .queryParam("appid", token)
               .build();

       String responseGL = restTemplate.getForObject(uriGeolocation.toUriString(), String.class);
       if (Objects.isNull(responseGL) || responseGL.trim().isEmpty()) {
           throw new EmptyResponseException("Error during response from GeoLocation service");
       }

       log.info("Response from geolocation service: {}", responseGL);
       return responseGL;
   }

    @Override
    @Scheduled(fixedRate = 60000) //1 minuto
    public void initPolling() throws NotValidRequestException {
        if (PollingService.status) {
            log.info("Started sendWebRequest method");

            CompletableFuture<Model> responseCt = CompletableFuture.supplyAsync(()-> {
                try {
                    return sendHttpReq("Catania");
                } catch (NotValidRequestException | EmptyResponseException e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture<Model> responseMi = CompletableFuture.supplyAsync(()-> {
                try {
                    return sendHttpReq("Milano");
                } catch (NotValidRequestException | EmptyResponseException e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture<Model> responseRm = CompletableFuture.supplyAsync(()-> {
                try {
                    return sendHttpReq("Roma");
                } catch (NotValidRequestException | EmptyResponseException e) {
                    throw new RuntimeException(e);
                }
            });

            CompletableFuture.allOf(responseCt,responseMi,responseRm).join();
            CompletableFuture.runAsync(() -> {
                try {
                    sendToFluentd(responseCt.get(), "Catania");
                }catch (InterruptedException | ExecutionException | NotValidRequestException e){
                    log.error("Error during send responseCT to fluentd method: {}",e.getMessage());
                }
            });
            CompletableFuture.runAsync(() -> {
                try {
                    sendToFluentd(responseRm.get(), "Roma");
                }catch (InterruptedException | ExecutionException | NotValidRequestException e){
                    log.error("Error during send responseCT to fluentd method: {}",e.getMessage());
                }
            });
            CompletableFuture.runAsync(() -> {
                try {
                    sendToFluentd(responseMi.get(), "Milano");
                }catch (InterruptedException | ExecutionException | NotValidRequestException e){
                    log.error("Error during send responseCT to fluentd method: {}",e.getMessage());
                }
            });

            log.info("Response sent to fluentd");
        }else{
            log.warn("Polling variable is disabled, able it to start the polling service :)");
        }
    }

    @Override
    public Optional<String> sendFakeJsonToTestKafka(String json) {
        log.info("Send message to kafka as test running..");
        try{
            UriComponents uri = UriComponentsBuilder.fromUriString("http://20.0.0.3:8081/catania")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

            ResponseEntity<String> response = restTemplate.exchange(uri.toUriString(),HttpMethod.POST,requestEntity,String.class);

            if(response.getStatusCode().is2xxSuccessful()){
                log.info("Message sent successfully!");
                return Optional.of("ok");
            }
        }catch (Exception e){
            log.error("Exception: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
