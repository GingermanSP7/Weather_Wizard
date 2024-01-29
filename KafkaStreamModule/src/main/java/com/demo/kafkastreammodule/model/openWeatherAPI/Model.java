package com.demo.kafkastreammodule.model.openWeatherAPI;

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
    private Rain rain;
    private Integer visibility;
    private Wind wind;
    private Clouds clouds;
    private String dt;
    private Sys sys;
    private String timezone;
    private String id;
    private String name;
    private Integer cod;
}
