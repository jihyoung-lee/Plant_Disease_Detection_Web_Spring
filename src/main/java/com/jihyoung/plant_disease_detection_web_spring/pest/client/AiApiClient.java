package com.jihyoung.plant_disease_detection_web_spring.pest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class AiApiClient
{
    private final WebClient webClient;


    public AiApiClient(
            @Value("${ai.api.url}")
            String baseUrl
    )
    {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public String predict(
            String cropName,
            MultipartFile file
    ) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("crop_name", cropName)
                        .queryParam("UploadFile",file)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
