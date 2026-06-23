package com.jihyoung.plant_disease_detection_web_spring.global.exception;

public class PestApiException extends RuntimeException {

    public PestApiException(String message) {
        super(message);
    }

    public PestApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
