package com.jihyoung.plant_disease_detection_web_spring.pest.dto.search;

import java.util.List;

public record PestSearchApiResult(
        int totalCount,
        int startPoint,
        int displayCount,
        List<PestSearchItem> list
) {
}
