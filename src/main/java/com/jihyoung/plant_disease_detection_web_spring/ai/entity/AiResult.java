package com.jihyoung.plant_disease_detection_web_spring.ai.entity;

import com.jihyoung.plant_disease_detection_web_spring.ai.dto.PredictionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ai_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ai_results_image_hash_crop",
                columnNames = {"image_hash", "sick_name_kor"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "image_hash", nullable = false, length = 64)
    private String imageHash;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "requested_crop_name", nullable = false, length = 50)
    private String requestedCropName;

    @Column(name = "crop_name", nullable = false, length = 50)
    private String cropName;

    @Column(name = "sick_name_kor", nullable = false, length = 100)
    private String sickNameKor;

    @Column(name = "sick_key", length = 100)
    private String sickKey;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal confidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction_status", nullable = false, length = 30)
    private PredictionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AiResult(
            String imageUrl,
            String imageHash,
            String originalFileName,
            String requestedCropName,
            String cropName,
            String sickNameKor,
            String sickKey,
            BigDecimal confidence,
            PredictionStatus status
    ) {
        this.imageUrl = imageUrl;
        this.imageHash = imageHash;
        this.originalFileName = originalFileName;
        this.requestedCropName = requestedCropName;
        this.cropName = cropName;
        this.sickNameKor = sickNameKor;
        this.sickKey = sickKey;
        this.confidence = confidence;
        this.status = status;
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
