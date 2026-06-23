package com.jihyoung.plant_disease_detection_web_spring.pest.client;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
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

    public AiPredictResponse predict(
            String cropName,
            MultipartFile image
    ) {
        MultipartBodyBuilder builder =
                new MultipartBodyBuilder();

        builder.part("crop_name", cropName);
        builder.part(
                "image",
                image.getResource()
        );


        return webClient.post()
                .uri("/predict")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("AI API 호출 실패: " + body))
                )
                .bodyToMono(AiPredictResponse.class)
                .block();

    }
}
