package com.demo.weatherwizard.model.openWeatherAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sys {
    private Integer type;
    private String id;
    private String country;
    private String sunrise;
    private String sunset;
}
