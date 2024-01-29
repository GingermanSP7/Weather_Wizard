package com.demo.kafkastreammodule.model.openWeatherAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coord {
    private Double lat;
    private Double lon;
}
