package com.demo.weatherwizard.model.openWeatherAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rain {
    @JsonProperty(value = "1h")
    private Double oneHour;
    @JsonProperty(value = "3h")
    private Double threeHour;
}
