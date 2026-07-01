package com.jihyoung.plant_disease_detection_web_spring.ai.entity;

import com.jihyoung.plant_disease_detection_web_spring.ai.dto.PredictionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ai_results"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_result_cache_id", nullable = false)
    private AiResultCache aiResultCache;

    public AiResult(
            String imageUrl,
            String originalFileName,
            AiResultCache aiResultCache
    ) {
        this.imageUrl = imageUrl;
        this.originalFileName = originalFileName;
        this.aiResultCache = aiResultCache;
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
