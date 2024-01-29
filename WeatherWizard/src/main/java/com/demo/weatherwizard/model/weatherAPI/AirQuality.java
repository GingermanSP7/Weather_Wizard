package com.demo.weatherwizard.model.weatherAPI;

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
    @JsonProperty(value = "gb-defra-index")
    private Double gbDefraIndex;

    @Override
    public String toString() {
        return "AirQuality{" +
                "co=" + co +
                ", no2=" + no2 +
                ", o3=" + o3 +
                ", so2=" + so2 +
                ", pm2_5=" + pm2_5 +
                ", pm10=" + pm10 +
                ", us-epa-index=" + usEpaIndex +
                ", gb-defra-index=" + gbDefraIndex +
                '}';
    }
}
