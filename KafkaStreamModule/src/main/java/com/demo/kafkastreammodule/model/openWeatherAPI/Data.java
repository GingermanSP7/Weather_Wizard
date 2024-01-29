package com.demo.kafkastreammodule.model.openWeatherAPI;

import lombok.Builder;

import java.util.List;

@lombok.Data
@Builder
public class Data {
    private Coord coord;
    private List<Weather> weatherList;
    private String base;
    private Main main;
    private Integer visibility;
    private Wind wind;
    private Clouds clouds;
    private Long dt;
    private Sys sys;
    private Long timezone;
    private Long id;
    private String name;
    private Integer cod;
}
