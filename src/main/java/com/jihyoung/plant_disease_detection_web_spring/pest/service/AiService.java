package com.jihyoung.plant_disease_detection_web_spring.pest.service;

import com.jihyoung.plant_disease_detection_web_spring.pest.client.AiApiClient;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.Set;

@Service
public class AiService {
    private final AiApiClient aiApiClient;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "potato",
            "tomato",
            "apple",
            "grape",
            "peach",
            "strawberry"
    );

    public AiService(AiApiClient aiApiClient) {
        this.aiApiClient = aiApiClient;
    }

    public AiPredictResponse predict (
            String cropName,
            MultipartFile image
    )
    {
        validate(cropName, image);
        return aiApiClient.predict(
                cropName,
                image
        );
    }

    private void validate(String cropName, MultipartFile image) {
        if (cropName == null || !SUPPORTED_CROPS.contains(cropName)) {
            throw new IllegalArgumentException("지원하지 않는 작물입니다.");
        }

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }
}
