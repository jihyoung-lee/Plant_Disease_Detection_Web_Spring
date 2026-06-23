package com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai;

public record AiPredictResponse (
        String cropName,
        String sickNameKor,
        double confidence
) {

}


