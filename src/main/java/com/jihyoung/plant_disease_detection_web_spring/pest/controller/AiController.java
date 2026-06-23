package com.jihyoung.plant_disease_detection_web_spring.pest.controller;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictResultResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.service.AiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AiController {

    private final AiService aiService;

    public AiController (AiService aiService)
    {
        this.aiService = aiService;
    }

    @PostMapping(value = "/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AiPredictResultResponse predict(
            @RequestParam("cropName") String cropName,
            @RequestParam("image") MultipartFile image
    ) {
        return aiService.predict(cropName, image);
    }
}
