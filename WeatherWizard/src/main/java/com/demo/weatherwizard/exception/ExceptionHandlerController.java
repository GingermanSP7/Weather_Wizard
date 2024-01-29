package com.demo.weatherwizard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ProblemDetail {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleEmptyResponseException(EmptyResponseException e){
        ErrorResponse error = new ErrorResponseException(
                HttpStatus.NO_CONTENT,
                ProblemDetail.forStatus(204),
                e.getCause(),
                e.getMsg(),
                null
        );
        return ResponseEntity.ok().body(error);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotValidRequestException(NotValidRequestException e){
        ErrorResponse error = new ErrorResponseException(
                HttpStatus.NO_CONTENT,
                ProblemDetail.forStatus(404),
                e.getCause(),
                e.getMsg(),
                null
        );
        return ResponseEntity.badRequest().body(error);
    }
}
