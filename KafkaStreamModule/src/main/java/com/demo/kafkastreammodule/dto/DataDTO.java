package com.demo.kafkastreammodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataDTO {
    private Double temp;
    private Double feltTemp;
    private Double maxTemp;
    private Double minTemp;

    private Double humidity;
    private Integer precipProbability;
    private Double rainIntensity;
    private Double windSpeed;
    private Double pressure;
    private String sunrise;
    private String sunset;

    private Double co;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double pm2_5;
    private Double pm10;
    @JsonProperty(value = "us-epa-index")
    private Double usEpaIndex;
    @JsonProperty(value = "us-defra-index")
    private Double gbDefraIndex;

    private String timestamp;
    private String latitude;
    private String longitude;
    private String city;
}
