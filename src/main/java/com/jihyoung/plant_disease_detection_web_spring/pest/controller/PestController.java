package com.jihyoung.plant_disease_detection_web_spring.pest.controller;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.service.PestService;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class PestController {

    private final PestService pestService;

    public PestController(PestService pestService) {
        this.pestService = pestService;
    }


    @GetMapping("/pest")
    public PestSearchResponse search(
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String sickNameKor,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "page는 1 이상이어야 합니다.") int page
    ) {
        return pestService.search(
                cropName,
                sickNameKor,
                page
        );
    }
    @GetMapping("/pest/{sickKey}")
    public PestInfoResponse info(
            @PathVariable String sickKey
    ) {
        return pestService.info(sickKey);
    }
}
