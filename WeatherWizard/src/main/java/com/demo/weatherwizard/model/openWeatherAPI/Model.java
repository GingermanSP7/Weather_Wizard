package com.demo.weatherwizard.model.openWeatherAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private Integer visibility;
    private Wind wind;
    private Rain rain;
    private Clouds clouds;
    private String dt;
    private Sys sys;
    private String timezone;
    private String id;
    private String name;
    private Integer cod;
}
