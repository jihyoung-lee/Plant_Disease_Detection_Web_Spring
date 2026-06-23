package com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;

public record AiPredictResultResponse(
        PredictionStatus status,
        String cropName,
        String sickNameKor,
        double confidence,
        String message,
        PestInfoResponse pestInfo
)
{

}

