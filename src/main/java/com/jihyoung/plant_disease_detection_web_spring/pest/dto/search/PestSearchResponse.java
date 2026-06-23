package com.jihyoung.plant_disease_detection_web_spring.pest.dto.search;

import java.util.List;

public record PestSearchResponse(
        int totalCount,
        int page,
        int displayCount,
        int totalPages,
        List<PestSearchItem> items
) {
}
