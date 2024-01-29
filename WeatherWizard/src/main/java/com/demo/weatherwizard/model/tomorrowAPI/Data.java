package com.demo.weatherwizard.model.tomorrowAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private String time;
    private Values values;
}
