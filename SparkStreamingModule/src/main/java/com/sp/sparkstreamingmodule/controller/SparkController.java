package com.sp.sparkstreamingmodule.controller;

import com.sp.sparkstreamingmodule.service.SparkServiceImpl;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/v0/spark/")
public class SparkController {
    private final SparkServiceImpl sparkService;

    public SparkController (){
        this.sparkService = new SparkServiceImpl();
    }

    @PostMapping(value = "/start")
    public ResponseEntity<String> startSpark() throws ExecutionException, InterruptedException {
        if(sparkService.startSpark()) {
//            sparkService.init();
            return ResponseEntity.ok().body("Spark started successfully!");
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(409)).body("Spark already actived");
    }

    @PostMapping(value = "/stop")
    public ResponseEntity<String> stopSpark(){
        if(sparkService.stopSpark()) return ResponseEntity.ok().body("Spark stopped!");
        return ResponseEntity.status(HttpStatusCode.valueOf(409)).body("Spark already stopped");
    }
}
