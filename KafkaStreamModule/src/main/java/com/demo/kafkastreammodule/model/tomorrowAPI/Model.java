package com.demo.kafkastreammodule.model.tomorrowAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    private Data data;
    private Location location;
}
