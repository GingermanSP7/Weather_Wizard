package com.demo.kafkastreammodule.model.openWeatherAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sys {
    private Integer type;
    private String id;
    private String country;
    private Long sunrise;
    private Long sunset;
}
