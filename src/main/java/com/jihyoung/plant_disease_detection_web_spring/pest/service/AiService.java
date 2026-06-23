package com.jihyoung.plant_disease_detection_web_spring.pest.service;

import com.jihyoung.plant_disease_detection_web_spring.pest.client.AiApiClient;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Service
public class AiService {
    private final AiApiClient aiApiClient;

    public AiService(AiApiClient aiApiClient) {
        this.aiApiClient = aiApiClient;
    }

    public AiPredictResponse predict (
            String cropName,
            MultipartFile image
    )
    {
        return aiApiClient.predict(
                cropName,
                image
        );
    }
}
