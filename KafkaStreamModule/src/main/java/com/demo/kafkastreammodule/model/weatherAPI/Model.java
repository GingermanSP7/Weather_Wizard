package com.demo.kafkastreammodule.model.weatherAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    private Location location;
    private Current current;

    @Override
    public String toString() {
        return "Data{" +
                "location=" + location +
                ", current=" + current +
                '}';
    }
}
