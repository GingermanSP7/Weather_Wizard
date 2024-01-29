package com.demo.kafkastreammodule.model.tomorrowAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double lat;
    private Double lon;
    private String name;
    private String type;
}
