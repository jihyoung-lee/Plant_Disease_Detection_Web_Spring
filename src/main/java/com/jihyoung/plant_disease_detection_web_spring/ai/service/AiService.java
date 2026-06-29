package com.jihyoung.plant_disease_detection_web_spring.ai.service;

import com.jihyoung.plant_disease_detection_web_spring.ai.client.AiApiClient;
import com.jihyoung.plant_disease_detection_web_spring.ai.dto.AiPredictApiResponse;
import com.jihyoung.plant_disease_detection_web_spring.ai.dto.AiPredictResultResponse;
import com.jihyoung.plant_disease_detection_web_spring.ai.dto.PredictionStatus;
import com.jihyoung.plant_disease_detection_web_spring.ai.entity.AiResult;
import com.jihyoung.plant_disease_detection_web_spring.ai.repository.AiResultRepository;
import com.jihyoung.plant_disease_detection_web_spring.global.exception.AiServerException;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.service.PestService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class AiService {

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "potato",
            "apple",
            "grape",
            "peach",
            "strawberry"
    );

    private static final Map<String, String> PEST_API_CROP_NAMES = Map.of(
            "potato", "감자",
            "apple", "사과",
            "grape", "포도",
            "peach", "복숭아",
            "strawberry", "딸기"
    );

    private static final String UNDETERMINED_SICK_NAME = "판단보류";

    private final AiApiClient aiApiClient;
    private final PestService pestService;
    private final AiResultRepository aiResultRepository;

    public AiService(
            AiApiClient aiApiClient,
            PestService pestService,
            AiResultRepository aiResultRepository
    ) {
        this.aiApiClient = aiApiClient;
        this.pestService = pestService;
        this.aiResultRepository = aiResultRepository;
    }

    public AiPredictResultResponse predict(String requestedCropName, MultipartFile image) {
        validate(requestedCropName, image);

        String imageHash = generateHash(image);
        Optional<AiResult> cachedResult = aiResultRepository
                .findByImageHashAndRequestedCropName(imageHash, requestedCropName);

        if (cachedResult.isPresent()) {
            return buildCachedResponse(cachedResult.get());
        }

        return predictAndStore(requestedCropName, image, imageHash);
    }

    private AiPredictResultResponse predictAndStore(
            String requestedCropName,
            MultipartFile image,
            String imageHash
    ) {
        AiPredictApiResponse response = aiApiClient.predict(requestedCropName, image);
        if (response == null || !hasText(response.cropName()) || !hasText(response.sickNameKor())) {
            throw new AiServerException("AI 서버가 유효한 예측 결과를 반환하지 않았습니다.");
        }

        if (UNDETERMINED_SICK_NAME.equals(response.sickNameKor())) {
            AiResult result = saveAiResult(
                    imageHash,
                    image.getOriginalFilename(),
                    requestedCropName,
                    response,
                    PredictionStatus.UNDETERMINED,
                    null
            );
            return toResponse(result, null, "판단이 어려운 이미지입니다. 다른 사진을 올려 주세요.");
        }

        Optional<String> sickKey = getSickKey(response.cropName(), response.sickNameKor());
        if (sickKey.isEmpty()) {
            AiResult result = saveAiResult(
                    imageHash,
                    image.getOriginalFilename(),
                    requestedCropName,
                    response,
                    PredictionStatus.INFO_NOT_FOUND,
                    null
            );
            return toResponse(result, null, "예측 결과에 맞는 병해충 상세 정보를 찾지 못했습니다.");
        }

        AiResult result = saveAiResult(
                imageHash,
                image.getOriginalFilename(),
                requestedCropName,
                response,
                PredictionStatus.SUCCESS,
                sickKey.get()
        );

        PestInfoResponse pestInfo = pestService.info(sickKey.get());
        return toResponse(result, pestInfo, "예측에 성공했습니다.");
    }

    private AiPredictResultResponse buildCachedResponse(AiResult result) {
        if (result.getStatus() != PredictionStatus.SUCCESS || !hasText(result.getSickKey())) {
            return toResponse(result, null, messageFor(result.getStatus()));
        }

        PestInfoResponse pestInfo = pestService.info(result.getSickKey());
        return toResponse(result, pestInfo, "저장된 예측 결과를 사용했습니다.");
    }

    private AiResult saveAiResult(
            String imageHash,
            String originalFileName,
            String requestedCropName,
            AiPredictApiResponse response,
            PredictionStatus status,
            String sickKey
    ) {
        AiResult result = new AiResult(
                null,
                imageHash,
                originalFileName,
                requestedCropName,
                response.cropName(),
                response.sickNameKor(),
                sickKey,
                response.confidence(),
                status
        );
        return aiResultRepository.save(result);
    }

    private AiPredictResultResponse toResponse(
            AiResult result,
            PestInfoResponse pestInfo,
            String message
    ) {
        return new AiPredictResultResponse(
                result.getStatus(),
                result.getCropName(),
                result.getSickNameKor(),
                result.getConfidence(),
                message,
                pestInfo
        );
    }

    private String messageFor(PredictionStatus status) {
        return switch (status) {
            case UNDETERMINED -> "판단이 어려운 이미지입니다. 다른 사진을 올려 주세요.";
            case INFO_NOT_FOUND -> "예측 결과에 맞는 병해충 상세 정보를 찾지 못했습니다.";
            case SUCCESS -> "저장된 예측 결과를 사용했습니다.";
        };
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

        String pestApiCropName = PEST_API_CROP_NAMES.getOrDefault(
                cropName.trim().toLowerCase(Locale.ROOT),
                cropName.trim()
        );
        String normalizedSickName = sickNameKor.trim();

        PestSearchResponse searchResponse = pestService.search(
                pestApiCropName,
                normalizedSickName,
                1
        );

        if (searchResponse == null || searchResponse.items() == null) {
            return Optional.empty();
        }

        return searchResponse.items().stream()
                .filter(Objects::nonNull)
                .filter(item -> sameText(pestApiCropName, item.cropName()))
                .filter(item -> sameText(normalizedSickName, item.sickNameKor()))
                .map(item -> item.sickKey())
                .filter(AiService::hasText)
                .findFirst();
    }

    private String generateHash(MultipartFile file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(file.getBytes());
            StringBuilder hash = new StringBuilder();

            for (byte hashByte : hashBytes) {
                hash.append(String.format("%02x", hashByte));
            }

            return hash.toString();
        } catch (Exception e) {
            throw new IllegalStateException("이미지 해시를 생성하지 못했습니다.", e);
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean sameText(String expected, String actual) {
        return hasText(actual) && expected.equals(actual.trim());
    }
}
