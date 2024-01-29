package com.sp.sparkstreamingmodule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Temperature {
    private Double temp;
    private Double feltTemp;
    private Double maxTemp;
    private Double minTemp;
    private String city;
    private String latitude;
    private String longitude;
    private String timestamp;
}
