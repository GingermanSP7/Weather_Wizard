package com.demo.weatherwizard.exception;

import lombok.Getter;

@Getter
public class EmptyResponseException extends Exception {
    private final String msg;

    public EmptyResponseException(String msg) {
        super(msg);
        this.msg = msg;
    }
}