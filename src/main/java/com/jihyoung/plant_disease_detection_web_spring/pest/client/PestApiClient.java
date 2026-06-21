package com.jihyoung.plant_disease_detection_web_spring.pest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PestApiClient {

    private final WebClient webClient;

    @Value("${pest.api.key}")
    private String apiKey;

    public PestApiClient(
            @Value("${pest.api.url}") String baseUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public String search(
            String cropName,
            String sickNameKor,
            int startPoint,
            int displayCount
    ) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apiKey", apiKey)
                        .queryParam("serviceCode", "SVC01")
                        .queryParam("serviceType", "AA003")
                        .queryParam("cropName", cropName)
                        .queryParam("sickNameKor", sickNameKor)
                        .queryParam("displayCount", displayCount)
                        .queryParam("startPoint", startPoint)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String info(
            String sickKey
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apiKey", apiKey)
                        .queryParam("serviceCode", "SVC05")
                        .queryParam("sickKey", sickKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
