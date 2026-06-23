package com.jihyoung.plant_disease_detection_web_spring.pest.service;

import com.jihyoung.plant_disease_detection_web_spring.pest.client.PestApiClient;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoApiResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchApiResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchApiResult;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchItem;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class PestService {

    private final PestApiClient pestApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PestService(PestApiClient pestApiClient) {
        this.pestApiClient = pestApiClient;
    }

    @Cacheable(
            value = "pestSearch",
            key = "#cropName + ':' + #sickNameKor + ':' + #page"
    )
    public PestSearchResponse search(
            String cropName,
            String sickNameKor,
            int page
    ) {

        int displayCount = 5;
        int startPoint = (page - 1) * displayCount + 1;

        String json = pestApiClient.search(cropName, sickNameKor, startPoint, displayCount);

        PestSearchApiResponse apiResponse =
                objectMapper.readValue(json, PestSearchApiResponse.class);

        PestSearchApiResult service = apiResponse == null ? null : apiResponse.service();
        if (service == null) {
            throw new IllegalStateException("병해충 검색 API 응답 형식이 올바르지 않습니다.");
        }

        int totalCount = service.totalCount();
        List<PestSearchItem> items = service.list() == null ? List.of() : service.list();

        int totalPages = (int) Math.ceil((double) totalCount / displayCount);

        return new PestSearchResponse(totalCount, page, displayCount, totalPages, items);
    }

    @Cacheable(
            value = "pestInfo",
            key = "#sickKey",
            condition = "#sickKey != null && !#sickKey.isBlank()"
    )
    public PestInfoResponse info(
            String sickKey
    ) {
        if (sickKey == null || sickKey.isBlank()) {
            return null;
        }
        String json = pestApiClient.getPestInfo(sickKey);

        PestInfoApiResponse apiResponse =
                objectMapper.readValue(json, PestInfoApiResponse.class);

        return apiResponse == null ? null : apiResponse.service();
    }
}
