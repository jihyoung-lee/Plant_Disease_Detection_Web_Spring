package com.jihyoung.plant_disease_detection_web_spring.global.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String code,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    public ErrorResponse(
            int status,
            String code,
            String message
    ) {
        this(
                status,
                code,
                message,
                LocalDateTime.now()
        );
    }
}
