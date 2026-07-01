package com.jihyoung.plant_disease_detection_web_spring.ai.entity;

import com.jihyoung.plant_disease_detection_web_spring.ai.dto.PredictionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ai_result_cache",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ai_results_image_hash_crop",
                columnNames = {"image_hash", "requested_crop_name"}
        )

)
@Getter
@NoArgsConstructor
public class AiResultCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="image_hash", nullable = false, length = 64)
    private String imageHash;

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

    @OneToMany(mappedBy = "aiResultCache")
    private List<AiResult> aiResults = new ArrayList<>();

    public AiResultCache(
            String imageHash,
            String requestedCropName,
            String cropName,
            String sickNameKor,
            String sickKey,
            BigDecimal confidence,
            PredictionStatus status
    ) {
        this.imageHash = imageHash;
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
