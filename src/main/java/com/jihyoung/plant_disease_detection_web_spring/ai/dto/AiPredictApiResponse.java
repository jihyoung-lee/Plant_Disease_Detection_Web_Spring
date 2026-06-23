package com.jihyoung.plant_disease_detection_web_spring.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiPredictApiResponse(

        @JsonProperty("crop_name")
        String cropName,

        @JsonProperty("sick_name_kor")
        String sickNameKor,

        double confidence
) {

}


