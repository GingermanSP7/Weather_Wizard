package com.sp.sparkstreamingmodule.schemas;

import lombok.Getter;

public class JsonSchemas {
    public static final String tempJsonSchema = "{\n" +
            "    \"type\" : \"object\",\n" +
            "    \"properties\" : {\n" +
            "        \"temp\" : {\"type\" : \"number\"},\n" +
            "        \"feltTemp\" : {\"type\" : \"number\"},\n" +
            "        \"maxTemp\" : {\"type\" : \"number\"},\n" +
            "        \"minTemp\" : {\"type\" : \"number\"},\n" +
            "        \"city\" : {\"type\" : \"string\"}\n" +
            "    },\n" +
            "    \"required\": [\"temp\", \"feltTemp\", \"maxTemp\", \"minTemp\", \"city\"]\n" +
            "}";


}
