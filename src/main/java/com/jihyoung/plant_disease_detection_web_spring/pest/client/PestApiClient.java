package com.jihyoung.plant_disease_detection_web_spring.pest.client;

import com.jihyoung.plant_disease_detection_web_spring.global.exception.PestApiException;
import com.jihyoung.plant_disease_detection_web_spring.global.exception.PestApiTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class PestApiClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;
    private final String apiKey;

    public PestApiClient(
            @Value("${pest.api.url}") String baseUrl,
            @Value("${pest.api.key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    public String search(
            String cropName,
            String sickNameKor,
            int startPoint,
            int displayCount
    ) {
        return await(
                webClient.get()
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
        );
    }

    public String getPestInfo(String sickKey) {
        return await(
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("apiKey", apiKey)
                                .queryParam("serviceCode", "SVC05")
                                .queryParam("sickKey", sickKey)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    private String await(Mono<String> response) {
        try {
            return response.block(REQUEST_TIMEOUT);
        } catch (WebClientResponseException e) {
            throw new PestApiException("병해충 API가 오류 응답을 반환했습니다.", e);
        } catch (WebClientRequestException e) {
            throw new PestApiException("병해충 API에 연결할 수 없습니다.", e);
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof TimeoutException) {
                throw new PestApiTimeoutException("병해충 API 응답 시간이 초과되었습니다.", e);
            }
            throw new PestApiException("병해충 API 응답을 처리하지 못했습니다.", e);
        } catch (RuntimeException e) {
            throw new PestApiException("병해충 API 호출 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }
}
