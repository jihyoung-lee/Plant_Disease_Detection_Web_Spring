package com.jihyoung.plant_disease_detection_web_spring.global.dto;

public record ErrorResponse(
        int status,
        String code,
        String message
) {
}
