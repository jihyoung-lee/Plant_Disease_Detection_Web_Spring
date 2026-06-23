package com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiPredictResponse (

        @JsonProperty("crop_name")
        String cropName,

        @JsonProperty("sick_name_kor")
        String sickNameKor,

        double confidence
) {

}


