package com.jihyoung.plant_disease_detection_web_spring.global.exception;

public class PestApiTimeoutException extends RuntimeException {

    public PestApiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
