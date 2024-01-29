package com.sp.sparkstreamingmodule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenInfo {
    private Integer precipProb;
    private Double rainIntensity;
    private Double windSpeed;
    private Double pressure;
    private String sunrise;
    private String sunset;
}
