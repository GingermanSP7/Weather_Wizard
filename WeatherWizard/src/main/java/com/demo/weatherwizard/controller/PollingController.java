package com.demo.weatherwizard.controller;

import com.demo.weatherwizard.service.polling.PollingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/v0/polling")
@Controller
public class PollingController {
    @PutMapping(value = "/activate")
    public ResponseEntity<String> activate(){
        if(PollingService.activate()){
            return ResponseEntity.ok().body("Polling variable activated successfully!");
        }
        return ResponseEntity.badRequest().body("Fail, polling variable is already activated");
    }

    @PutMapping(value = "/disable")
    public ResponseEntity<String> disable(){
        if(!PollingService.disable()){
            return ResponseEntity.ok().body("Polling variable disabled successfully!");
        }
        return ResponseEntity.badRequest().body("Fail, polling variable is already disabled");
    }
}
