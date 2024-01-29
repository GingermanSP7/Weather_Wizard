package com.demo.weatherwizard.service.polling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PollingService {
    public static Boolean status;

    PollingService(){
        status = false;
    }

    public static Boolean activate() {
        if(!status){
            status = true;
            log.info("Changed polling variable to: {}", true);
            return true;
        }
        log.error("Fail, polling variable is already able");
        return false;
    }

    public static Boolean disable() {
        if(status){
            status = false;
            log.info("Changed polling variable to: {}", false);
            return false;
        }
        log.error("Fail, polling variable is already disabled");
        return true;
    }
}
