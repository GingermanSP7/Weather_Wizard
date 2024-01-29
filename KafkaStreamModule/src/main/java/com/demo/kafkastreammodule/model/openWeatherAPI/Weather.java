package com.demo.kafkastreammodule.model.openWeatherAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private Integer id;
    private String main;
    private String description;
    private String icon;
}
