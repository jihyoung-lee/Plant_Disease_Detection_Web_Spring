package com.jihyoung.plant_disease_detection_web_spring.pest.dto;

import lombok.Getter;

@Getter
public class PestItem {

    private String cropName;
    private String thumbImg;
    private String sickNameKor;
    private String sickKey;

    public PestItem (String cropName, String thumbImg, String sickNameKor, String sickKey){
        this.cropName = cropName;
        this.thumbImg = thumbImg;
        this.sickNameKor = sickNameKor;
        this.sickKey = sickKey;
    }
}
