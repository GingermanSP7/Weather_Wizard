package com.sp.sparkstreamingmodule.mapping;

public class ElasticMapping {

    public static final String elasticMapping = "{\n" +
            "  \"properties\": {\n" +
            "    \"temp\": { \"type\": \"double\" },\n" +
            "    \"feltTemp\": { \"type\": \"double\" },\n" +
            "    \"maxTemp\": { \"type\": \"double\" },\n" +
            "    \"minTemp\": { \"type\": \"double\" },\n" +
            "    \"city\": { \"type\": \"keyword\" },\n" +
            "    \"latitude\": { \"type\": \"keyword\" },\n" +
            "    \"longitude\": { \"type\": \"keyword\" },\n" +
            "    \"location\": { \"type\": \"geo_point\" },\n" +
            "    \"timestamp\": { \"type\": \"date\" },\n" +
            "    \"humidity\": { \"type\": \"double\" },\n" +
            "    \"windSpeed\": { \"type\": \"double\" },\n" +
            "    \"pressure\": { \"type\": \"double\" },\n" +
            "    \"sunrise\": { \"type\": \"date\" },\n" +
            "    \"sunset\": { \"type\": \"date\" },\n" +
            "    \"rainIntensity\": { \"type\": \"double\" },\n" +
            "    \"rainProb\": { \"type\": \"keyword\" },\n" +
            "    \"co\": { \"type\": \"double\" },\n" +
            "    \"no2\": { \"type\": \"double\" },\n" +
            "    \"o3\": { \"type\": \"double\" },\n" +
            "    \"so2\": { \"type\": \"double\" },\n" +
            "    \"pm2_5\": { \"type\": \"double\" },\n" +
            "    \"pm10\": { \"type\": \"double\" },\n" +
            "    \"usEpaIndex\": { \"type\": \"integer\" },\n" +
            "    \"gbDefraIndex\": { \"type\": \"integer\" },\n" +
//            "    \"features\": { \"type\": \"nested\", \"properties\": { \"feature\": { \"type\": \"double\" } } },\n" +
            "    \"prediction\": { \"type\": \"double\" }\n" +
            "  }\n" +
            "}";
}
