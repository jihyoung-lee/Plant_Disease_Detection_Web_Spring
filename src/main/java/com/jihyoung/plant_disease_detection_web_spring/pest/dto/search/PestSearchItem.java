package com.jihyoung.plant_disease_detection_web_spring.pest.dto.search;

public record PestSearchItem(
        String cropName,
        String thumbImg,
        String sickNameKor,
        String sickKey
) {
}
