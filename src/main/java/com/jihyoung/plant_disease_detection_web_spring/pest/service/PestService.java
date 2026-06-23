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

import static org.springframework.util.StringUtils.hasText;

@Service
public class PestService {

    private final PestApiClient pestApiClient;
    private final ObjectMapper objectMapper;

    public PestService(
            PestApiClient pestApiClient,
            ObjectMapper objectMapper) {
        this.pestApiClient = pestApiClient;
        this.objectMapper = objectMapper;
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

        validateSearch(
                cropName,
                sickNameKor,
                page);

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
    private void validateSearch(
            String cropName,
            String sickNameKor,
            int page
    ) {
        if (!hasText(cropName) && !hasText(sickNameKor)) {
            throw new IllegalArgumentException(
                    "작물명 또는 병명 중 하나는 입력해야 합니다."
            );
        }

        if (page < 1) {
            throw new IllegalArgumentException(
                    "page는 1 이상이어야 합니다."
            );
        }
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
