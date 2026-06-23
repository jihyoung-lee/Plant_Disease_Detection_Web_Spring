package com.jihyoung.plant_disease_detection_web_spring.pest.dto.info;

import java.util.List;
import java.util.Objects;

public record PestInfoResponse(
        String cropName,
        String sickNameKor,
        String sickNameChn,
        String sickNameEng,
        String infectionRoute,
        String developmentCondition,
        String symptoms,
        String preventionMethod,
        String biologyPrvnbeMth,
        String chemicalPrvnbeMth,
        List<PestInfoImageItem> imageList
) {
    public PestInfoResponse {
        cropName = normalize(cropName);
        sickNameKor = normalize(sickNameKor);
        sickNameChn = normalize(sickNameChn);
        sickNameEng = normalize(sickNameEng);
        infectionRoute = normalize(infectionRoute);
        developmentCondition = normalize(developmentCondition);
        symptoms = normalize(symptoms);
        preventionMethod = normalize(preventionMethod);
        biologyPrvnbeMth = normalize(biologyPrvnbeMth);
        chemicalPrvnbeMth = normalize(chemicalPrvnbeMth);
        imageList = imageList == null
                ? List.of()
                : imageList.stream().filter(Objects::nonNull).toList();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? "" : value;
    }
}
