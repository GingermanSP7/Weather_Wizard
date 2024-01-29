package com.demo.weatherwizard.openweatherapi;

import com.demo.weatherwizard.exception.EmptyResponseException;
import com.demo.weatherwizard.exception.NotValidRequestException;
import com.demo.weatherwizard.service.APIService;
import com.demo.weatherwizard.service.openweatherAPI.OpenWeatherAPIService;
import com.demo.weatherwizard.service.openweatherAPI.OpenWeatherAPIServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OpenWeatherApiTests {
    OpenWeatherAPIService openWeatherAPIService;
    APIService apiService;
    @Mock
    Environment env;
    @Mock
    RestTemplate restTemplate;
    @Mock
    UriComponents uriGeoLocation;

    @BeforeEach
    public void beforeEachTest(){
        openWeatherAPIService = new OpenWeatherAPIServiceImpl(env,restTemplate);
        apiService = new OpenWeatherAPIServiceImpl(env,restTemplate);
    }

    @Test
    public void whenSendRequestLocation_thenReturnLocationInfoCT() throws IOException, EmptyResponseException, NotValidRequestException {
        String baseUrl = "http://api.openweathermap.org/geo/1.0/direct";
        String expected = "{\n" +
                "        \"name\": \"Catania\",\n" +
                "        \"local_names\": {\n" +
                "            \"ca\": \"Catània\",\n" +
                "            \"es\": \"Catania\",\n" +
                "            \"pt\": \"Catânia\",\n" +
                "            \"ar\": \"قطانية\",\n" +
                "            \"pl\": \"Katania\",\n" +
                "            \"el\": \"Κατάνια\",\n" +
                "            \"eo\": \"Katanio\",\n" +
                "            \"uk\": \"Катанія\",\n" +
                "            \"ru\": \"Катания\",\n" +
                "            \"he\": \"קטניה\",\n" +
                "            \"cs\": \"Katánie\",\n" +
                "            \"it\": \"Catania\",\n" +
                "            \"en\": \"Catania\",\n" +
                "            \"la\": \"Catina\",\n" +
                "            \"fr\": \"Catane\",\n" +
                "            \"bg\": \"Катания\",\n" +
                "            \"be\": \"Катанія\",\n" +
                "            \"zh\": \"卡塔尼亞\",\n" +
                "            \"fi\": \"Catania\"\n" +
                "        },\n" +
                "        \"lat\": 37.5023612,\n" +
                "        \"lon\": 15.0873718,\n" +
                "        \"country\": \"IT\",\n" +
                "        \"state\": \"Sicily\"\n" +
                "    }";
        when(env.getProperty("web.request.openweatherapi.geo.url")).thenReturn(baseUrl);
        when(env.getProperty("web.request.openweatherapi.token")).thenReturn("90101cc6bb660c9755ba261431b20374");
        when(env.getProperty("web.request.openweatherapi.geo.limit")).thenReturn("1");
        when(restTemplate.getForObject(
                uriGeoLocation.toUriString(),
                String.class
        )).thenReturn(expected);

        String response = openWeatherAPIService.sendRequestLocation("catania");

        assertEquals(expected,response);
    }

    @Test
    public void whenSendRequestLocation_throwNotValidRequestException() {
        when(env.getProperty("web.request.openweatherapi.geo.url")).thenReturn(null);

        Throwable exception = assertThrows(NotValidRequestException.class,()->openWeatherAPIService.sendRequestLocation("catania"));
        assertEquals("Url not present", exception.getMessage());
    }

    @Test
    public void whenSendRequestLocation_throwEmptyResponseException(){
        String baseUrl = "http://api.openweathermap.org/geo/1.0/direct";
        uriGeoLocation = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", "catania")
                .queryParam("limit", "1")
                .queryParam("appid", "90101cc6bb660c9755ba261431b20374")
                .build();

        when(env.getProperty("web.request.openweatherapi.geo.url")).thenReturn(baseUrl);
        when(env.getProperty("web.request.openweatherapi.token")).thenReturn("90101cc6bb660c9755ba261431b20374");
        when(env.getProperty("web.request.openweatherapi.geo.limit")).thenReturn("1");
        when(restTemplate.getForObject(
                uriGeoLocation.toUriString(),
                String.class
        )).thenReturn(null);

        Throwable exception = assertThrows(EmptyResponseException.class,()->openWeatherAPIService.sendRequestLocation("catania"));
        assertEquals("Error during response from GeoLocation service", exception.getMessage());
    }

    @Test
    public void whenInitPollingOnCatania_thenSuccess() throws IOException, EmptyResponseException, NotValidRequestException {
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
        String token = "90101cc6bb660c9755ba261431b20374";
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("lat", "37.5023612")
                .queryParam("lon", "15.0873718")
                .queryParam("appid", token)
                .queryParam("units",env.getProperty("web.request.openweatherapi.units"))
                .queryParam("lang",env.getProperty("web.request.openweatherapi.lang"))
                .build();

        when(env.getProperty("web.request.openweatherapi.url")).thenReturn(baseUrl);
        when(env.getProperty("web.request.openweatherapi.token")).thenReturn(token);
        when(env.getProperty("web.request.geo.catania.lat")).thenReturn("37.5023612");
        when(env.getProperty("web.request.geo.catania.lon")).thenReturn("15.0873718");
//        when(env.getProperty("web.request.geo.milano.lat")).thenReturn("45.4627042");
//        when(env.getProperty("web.request.geo.milano.lon")).thenReturn("9.0953316");
//        when(env.getProperty("web.request.geo.roma.lat")).thenReturn("41.8933203");
//        when(env.getProperty("web.request.geo.roma.lon")).thenReturn("12.4829321");

        when(restTemplate.getForObject(uriComponents.toUriString(),String.class));

        apiService.initPolling();
    }
}
