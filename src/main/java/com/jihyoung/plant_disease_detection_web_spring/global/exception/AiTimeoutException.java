package com.jihyoung.plant_disease_detection_web_spring.global.exception;

public class AiTimeoutException
        extends RuntimeException {

    public AiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
