package com.jihyoung.plant_disease_detection_web_spring.ai.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ai_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ai_results_image_hash",
                columnNames = "image_hash"
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

    @Column(name = "crop_name", nullable = false, length = 50)
    private String cropName;

    @Column(name = "disease_name_kor", nullable = false, length = 100)
    private String diseaseNameKor;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal confidence;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AiResult(
            String imageUrl,
            String imageHash,
            String originalFileName,
            String cropName,
            String diseaseNameKor,
            BigDecimal confidence
    ) {
        this.imageUrl = imageUrl;
        this.imageHash = imageHash;
        this.originalFileName = originalFileName;
        this.cropName = cropName;
        this.diseaseNameKor = diseaseNameKor;
        this.confidence = confidence;
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
