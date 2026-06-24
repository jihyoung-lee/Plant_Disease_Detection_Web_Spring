package com.jihyoung.plant_disease_detection_web_spring.ai.repository;

import com.jihyoung.plant_disease_detection_web_spring.ai.entity.AiResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiResultRepository
        extends JpaRepository<AiResult, Long> {

    Optional<AiResult> findByImageHashAndRequestedCropName(String imageHash, String requestedCropName);
}
