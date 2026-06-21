package com.jihyoung.plant_disease_detection_web_spring.pest.service;

import com.jihyoung.plant_disease_detection_web_spring.pest.client.PestApiClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.PestItem;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.PestResponse;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class PestService {

    private final PestApiClient pestApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PestService(PestApiClient pestApiClient) {
        this.pestApiClient = pestApiClient;
    }

    public PestResponse search(
            String cropName,
            String sickNameKor,
            int page
    ) {

        int displayCount = 5;
        int startPoint = (page - 1) * displayCount + 1;

        String json = pestApiClient.search(cropName, sickNameKor, startPoint, displayCount);
        // jackson objectmapper 객체 생성
        JsonNode root = objectMapper.readTree(json);
        JsonNode service = root.get("service");
        int totalCount = service.get("totalCount").asInt();
        JsonNode list = service.get("list");
        List<PestItem> items = StreamSupport.stream(
                                list.spliterator(),
                                false
                        )
                        .map(item -> new PestItem(
                                item.get("cropName").asString(),
                                item.get("thumbImg").asString(),
                                item.get("sickNameKor").asString(),
                                item.get("sickKey").asString()
                        ))
                        .toList();


        return new PestResponse(totalCount, items);
    }
}
