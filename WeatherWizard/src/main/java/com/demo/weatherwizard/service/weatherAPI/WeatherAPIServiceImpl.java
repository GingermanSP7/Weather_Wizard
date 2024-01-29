package com.demo.weatherwizard.service.weatherAPI;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import com.demo.weatherwizard.model.weatherAPI.Model;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service("WeatherAPIServiceImpl")
@Slf4j
public class WeatherAPIServiceImpl implements APIService {
    private final Environment env;
    private final RestTemplate restTemplate;

    private void sendToFluentd(Model json, String topic) throws NotValidRequestException {
        if(Objects.isNull(json)){
            throw new NotValidRequestException("Not Valid Json to send to Fluentd");
        }
        log.debug("Starting send to fluentd method");

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

            log.info("Message from WeatherAPI sent to fluent");
        }catch (NullPointerException e){
            log.error("NullPointerException: {}", e.getMessage());
        } catch (RestClientException e){
            log.error("RestClientException: {}", e.getMessage());
        }
    }

    private Model sendHttpReq(String city) throws NotValidRequestException, EmptyResponseException {
        String baseUrl = env.getProperty("web.request.weatherapi.url");
        String token = env.getProperty("web.request.weatherapi.token");

        log.debug("URL for API request: {}", baseUrl);
        if(Objects.isNull(baseUrl) || baseUrl.trim().isEmpty()){
            throw new NotValidRequestException("Url not present");
        }

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/current.json")
                .queryParam("key", token)
                .queryParam("q", city)
                .queryParam("aqi", env.getProperty("web.request.weatherapi.current.aqi"))
                .queryParam("lang",env.getProperty("web.request.weatherapi.current.lang"))
                .queryParam("tp",env.getProperty("web.request.weatherapi.current.tp"))
                .build();

        String url = uriBuilder.toUriString();
        log.debug("Final URL for API request: {}", url);

        try{
            ObjectMapper mapper = new ObjectMapper();
            Model response = mapper.readValue(restTemplate.getForObject(url, String.class), Model.class);
            if(Objects.isNull(response)){
                throw new EmptyResponseException("Response is empty");
            }

            log.debug("Ended sendWebRequest method");
            return response;
        }catch (RestClientException e){
            log.error("Error during request to the API, {}", e.getMessage());
        }catch (JsonProcessingException e) {
            log.error("Exception during mapping response from WeatherAPI: {}", e.getMessage());
        }
        return null;
    }

    @Autowired
    public WeatherAPIServiceImpl(
            Environment env,
            RestTemplate restTemplate
    ){
        this.env = env;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 80000)
    public void initPolling() throws NotValidRequestException, EmptyResponseException {
        if(PollingService.status){
            log.debug("Started sendWebRequest method");

            CompletableFuture<Model> responseCt = CompletableFuture.supplyAsync(() -> {
                try{
                    return sendHttpReq("Catania");
                } catch (EmptyResponseException | NotValidRequestException e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture<Model> responseMi = CompletableFuture.supplyAsync(() -> {
                try{
                    return sendHttpReq("Milan");
                } catch (EmptyResponseException | NotValidRequestException e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture<Model> responseRm = CompletableFuture.supplyAsync(() -> {
                try{
                    return sendHttpReq("Rome");
                } catch (EmptyResponseException | NotValidRequestException e) {
                    throw new RuntimeException(e);
                }
            });

            CompletableFuture.allOf(responseCt,responseMi,responseRm);
            log.info("Sending message to fluentd..");

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
        }
    }

    public Optional<String> sendFakeJsonToTestKafka(String json) {
        try{
            UriComponents uri = UriComponentsBuilder.fromUriString("http://20.0.0.3:8081/roma")
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
