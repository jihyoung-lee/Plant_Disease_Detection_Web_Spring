package com.jihyoung.plant_disease_detection_web_spring.global.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String code,
        String message,
        LocalDateTime timestamp
) {
}
