package com.jihyoung.plant_disease_detection_web_spring.ai.repository;

import com.jihyoung.plant_disease_detection_web_spring.ai.entity.AiResult;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AiResultRepository
        extends JpaRepository<AiResult, Long> {


}
