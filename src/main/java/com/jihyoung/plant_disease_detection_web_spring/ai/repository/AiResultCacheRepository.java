package com.jihyoung.plant_disease_detection_web_spring.ai.repository;

import com.jihyoung.plant_disease_detection_web_spring.ai.entity.AiResultCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiResultCacheRepository
        extends JpaRepository<AiResultCache, Long> {

    Optional<AiResultCache>
    findByImageHashAndRequestedCropName(
            String imageHash,
            String requestedCropName
    );
}