package com.demo.weatherwizard.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

@Slf4j
public class IOUtils {
    public static void writeJsonFile(String response) throws IOException {
        File file = new File(
                "./output/response_"+new Timestamp(System.currentTimeMillis())+".json"
        );
        if(file.createNewFile()){
            log.info("File created successfully!");
        }
        try(FileWriter writer = new FileWriter(file)){
            writer.write(response);
        }catch (IOException e){
            log.error("Error during writing file: {}", e.getMessage());
        }
    }


}
