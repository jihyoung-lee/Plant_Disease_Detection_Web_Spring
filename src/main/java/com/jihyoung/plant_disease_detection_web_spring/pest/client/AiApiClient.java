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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class AiApiClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private final WebClient webClient;

    public AiApiClient(@Value("${ai.api.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public AiPredictApiResponse predict(String cropName, MultipartFile image) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("crop_name", cropName);
        builder.part("image", image.getResource());

        return await(
                webClient.post()
                        .uri("/predict")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .bodyValue(builder.build())
                        .retrieve()
                        .bodyToMono(AiPredictApiResponse.class)
        );
    }

    private <T> T await(Mono<T> response) {
        try {
            return response.block(REQUEST_TIMEOUT);
        } catch (WebClientResponseException e) {
            throw new AiServerException("AI 서버가 오류 응답을 반환했습니다.", e);
        } catch (WebClientRequestException e) {
            throw new AiServerException("AI 서버에 연결할 수 없습니다.", e);
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof TimeoutException) {
                throw new AiTimeoutException("AI 서버 응답 시간이 초과되었습니다.", e);
            }
            throw new AiServerException("AI 서버 응답을 처리하지 못했습니다.", e);
        } catch (RuntimeException e) {
            throw new AiServerException("AI 서버 호출 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }
}
