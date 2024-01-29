package com.sp.sparkstreamingmodule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirQuality {
    private Double co;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double pm2_5;
    private Double pm10;
    @JsonProperty(value = "us-epa-index")
    private Double usEpaIndex;
    @JsonProperty(value = "us-defra-index")
    private Double gbDefraIndex;
}
