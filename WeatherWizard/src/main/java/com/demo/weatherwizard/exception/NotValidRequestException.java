package com.demo.weatherwizard.exception;

import lombok.Getter;

@Getter
public class NotValidRequestException extends Exception{
    private final String msg;

    public NotValidRequestException(String msg){
        super(msg);
        this.msg = msg;
    }
}
