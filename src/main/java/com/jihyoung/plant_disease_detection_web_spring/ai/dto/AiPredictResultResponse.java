package com.jihyoung.plant_disease_detection_web_spring.ai.dto;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;

import java.math.BigDecimal;

public record AiPredictResultResponse(
        PredictionStatus status,
        String cropName,
        String sickNameKor,
        BigDecimal confidence,
        String message,
        PestInfoResponse pestInfo
)
{

}

