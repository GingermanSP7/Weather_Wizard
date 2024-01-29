package com.demo.weatherwizard.service.tomorrowAPI;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import com.demo.weatherwizard.model.tomorrowAPI.Model;
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

@Service("TomorrowAPIServiceImpl")
@Slf4j
public class TomorrowAPIServiceImpl implements APIService {

    private final Environment env;
    private final RestTemplate restTemplate;

    private void sendToFluentd(Model json, String topic) throws NotValidRequestException {
        if(Objects.isNull(json) ){
            throw new NotValidRequestException("Not Valid Json to send to Fluentd");
        }
        log.info("Starting send to fluentd method");

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
            log.info("Ended sendToFluentd method for TomorrowAPI response");
        }catch (NullPointerException | RestClientException e){
            log.error("Exception: {}", e.getMessage());
        }
    }

    private Model sendHttpReq(String city) throws NotValidRequestException, EmptyResponseException {
        String baseUrl = env.getProperty("web.request.tomorrowapi.url");
        if(Objects.isNull(baseUrl) || baseUrl.isEmpty()){
            throw new NotValidRequestException("Url not present");
        }

        UriComponents uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("apikey",env.getProperty("web.request.tomorrowapi.token"))
                .queryParam("location",city)
                .build();
        log.debug("Final URL for API request: {}", uri.toUri());

        try{
            ObjectMapper mapper = new ObjectMapper();
            Model response = mapper.readValue(restTemplate.getForObject(uri.toUri(), String.class), Model.class);
            if(Objects.isNull(response)){
                throw new EmptyResponseException("Error in response from TomorrowAPI");
            }
            log.debug("HTTP response from tomorrowAPI {}", response);
            return response;
        }catch (HttpClientErrorException e){
            log.error("Error during HTTP response from TomorrowAPI: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Exception during mapping response from TomorrowAPI: {}", e.getMessage());
        }
        return null;
    }

    @Autowired
    public TomorrowAPIServiceImpl(
            Environment env,
            RestTemplate restTemplate
    ){
        this.env = env;
        this.restTemplate = restTemplate;
    }

    @Override
    @Scheduled(fixedRate = 120000) // 120 secondi
    public void initPolling() throws NotValidRequestException, EmptyResponseException {
        if(PollingService.status){
            log.info("Starting TomorrowAPI polling");

            CompletableFuture<Model> responseCt = CompletableFuture.supplyAsync(() -> {
                try{
                    return sendHttpReq("Catania");
                } catch (EmptyResponseException | NotValidRequestException e) {
                    throw new RuntimeException(e);
                }
            });

            CompletableFuture.allOf(responseCt);
            log.info("Sending message to fluentd..");

            CompletableFuture.runAsync(() -> {
                try {
                    sendToFluentd(responseCt.get(), "Catania");
                }catch (InterruptedException | ExecutionException | NotValidRequestException e){
                    log.error("Error during send responseCT to fluentd method: {}",e.getMessage());
                }
            });

            log.info("Response sent to fluentd");
        }
    }

    @Override
    public Optional<String> sendFakeJsonToTestKafka(String json){
        try{
            UriComponents uri = UriComponentsBuilder.fromUriString("http://20.0.0.3:8081/milano")
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
