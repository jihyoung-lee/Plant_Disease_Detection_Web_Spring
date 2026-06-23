package com.jihyoung.plant_disease_detection_web_spring.pest.client;

import com.jihyoung.plant_disease_detection_web_spring.global.exception.AiServerException;
import com.jihyoung.plant_disease_detection_web_spring.global.exception.AiTimeoutException;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;



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

    public AiPredictApiResponse predict(
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


        try {
            return webClient.post()
                    .uri("/predict")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(AiPredictApiResponse.class)
                    //타임아웃
                    .block(Duration.ofSeconds(15));
        } catch (WebClientRequestException e) {
            throw new AiTimeoutException();
        } catch (WebClientResponseException e) {
            throw new AiServerException();
        }

    }
}
