package com.demo.kafkastreammodule.model.tomorrowAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Values {
    private Double cloudBase;
    private Double cloudCeiling;
    private Integer cloudCover;
    private Double dewPoint;
    private Integer freezingRainIntensity;
    private Integer humidity;
    private Integer precipitationProbability;
    private Double pressureSurfaceLevel;
    private Integer rainIntensity;
    private Integer sleetIntensity;
    private Integer snowIntensity;
    private Double temperature;
    private Double temperatureApparent;
    private Double uvHealthConcern;
    private Double uvIndex;
    private Double visibility;
    private Double weatherCode;
    private Double windDirection;
    private Double windGust;
    private Double windSpeed;
}
