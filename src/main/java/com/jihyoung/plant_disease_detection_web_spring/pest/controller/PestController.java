package com.jihyoung.plant_disease_detection_web_spring.pest.controller;

import com.jihyoung.plant_disease_detection_web_spring.pest.dto.info.PestInfoResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.dto.search.PestSearchResponse;
import com.jihyoung.plant_disease_detection_web_spring.pest.service.PestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PestController {

    private final PestService pestService;

    public PestController(PestService pestService) {
        this.pestService = pestService;
    }

    @GetMapping("/pest/search")
    public PestSearchResponse search(
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String sickNameKor,
            @RequestParam(defaultValue = "1") int page
    ) {
        return pestService.search(
                cropName,
                sickNameKor,
                page
        );
    }

    @GetMapping("/pest/info")
    public PestInfoResponse info(
            @RequestParam(defaultValue = "D00001537") String sickKey
    ) {
        return pestService.info(sickKey);
    }
}
