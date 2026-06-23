package com.jihyoung.plant_disease_detection_web_spring.pest.dto.info;

public record PestInfoImageItem(
        String image,
        String iemSpchcknCode,
        String iemSpchcknNm,
        String imageTitle
) {
    public PestInfoImageItem {
        image = normalize(image);
        iemSpchcknCode = normalize(iemSpchcknCode);
        iemSpchcknNm = normalize(iemSpchcknNm);
        imageTitle = normalize(imageTitle);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? "" : value;
    }
}
