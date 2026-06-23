package com.jihyoung.plant_disease_detection_web_spring.pest.service;

import com.jihyoung.plant_disease_detection_web_spring.pest.client.AiApiClient;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictApiResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.AiPredictResultResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.ai.PredictionStatus;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class AiService {
    private final AiApiClient aiApiClient;
    private final PestService pestService;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "potato",
            "apple",
            "grape",
            "peach",
            "strawberry"
    );

    private static final String UNDETERMINED_SICK_NAME = "판단보류";


    public AiService(AiApiClient aiApiClient, PestService pestService) {
        this.aiApiClient = aiApiClient;
        this.pestService = pestService;
    }

    public AiPredictResultResponse predict(
            String cropName,
            MultipartFile image
    ) {
        validate(cropName, image);

        AiPredictApiResponse response = aiApiClient.predict(cropName, image);
        if (response == null || !hasText(response.cropName()) || !hasText(response.sickNameKor())) {
            throw new IllegalStateException("AI 서버가 유효한 예측 결과를 반환하지 않았습니다.");
        }

        // 판단보류는 병해충 API에 존재하는 질병명이 아니므로 조회하지 않는다.
        if (UNDETERMINED_SICK_NAME.equals(response.sickNameKor())) {
            return new AiPredictResultResponse(
                    PredictionStatus.UNDETERMINED,
                    response.cropName(),
                    response.sickNameKor(),
                    response.confidence(),
                    "사진을 다시 촬영하거나 다른 잎 사진을 올려 주세요.",
                    null
            );
        }

        Optional<String> sickKey = getSickKey(response.cropName(), response.sickNameKor());
        if (sickKey.isEmpty()) {
            return new AiPredictResultResponse(
                    PredictionStatus.INFO_NOT_FOUND,
                    response.cropName(),
                    response.sickNameKor(),
                    response.confidence(),
                    "예측 결과에 맞는 병해충 상세 정보를 찾지 못했습니다.",
                    null
            );
        }

        PestInfoResponse pestInfoResponse = pestService.info(sickKey.get());
        if (pestInfoResponse == null) {
            return new AiPredictResultResponse(
                    PredictionStatus.INFO_NOT_FOUND,
                    response.cropName(),
                    response.sickNameKor(),
                    response.confidence(),
                    "병해충 상세 정보를 불러오지 못했습니다.",
                    null
            );
        }

        return new AiPredictResultResponse(
                PredictionStatus.SUCCESS,
                response.cropName(),
                response.sickNameKor(),
                response.confidence(),
                "예측에 성공했습니다.",
                pestInfoResponse
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

    private Optional<String> getSickKey(String cropName, String sickNameKor) {
        if (!hasText(cropName) || !hasText(sickNameKor)) {
            return Optional.empty();
        }

        String normalizedSickName = sickNameKor.trim();

        PestSearchResponse searchResponse = pestService.search(
                cropName,
                normalizedSickName,
                1
        );

        if (searchResponse == null || searchResponse.items() == null) {
            return Optional.empty();
        }

        return searchResponse.items().stream()
                .filter(Objects::nonNull)
                .filter(item -> sameText(cropName, item.cropName()))
                .filter(item -> sameText(normalizedSickName, item.sickNameKor()))
                .map(item -> item.sickKey())
                .filter(AiService::hasText)
                .findFirst();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean sameText(String expected, String actual) {
        return hasText(actual) && expected.equals(actual.trim());
    }
}
