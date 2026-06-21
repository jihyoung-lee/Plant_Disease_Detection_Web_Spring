package com.jihyoung.plant_disease_detection_web_spring.pest.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PestResponse {

    private int totalCount;
    private List<PestItem>items;

    public PestResponse(int totalCount, List<PestItem> items) {
    this.totalCount = totalCount;
    this.items = items;
    }

}
