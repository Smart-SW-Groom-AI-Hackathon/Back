package com.goorm.team3.controller;

import com.goorm.team3.service.HamoService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hamo")
@RequiredArgsConstructor
public class HamoController {

    private final HamoService hamoService;


    @PostMapping("/simulate")
    public HamoService.OtterResult hamoUpdate(@RequestBody HamoUpdateDto dto) {
        String prompt = String.format("키: %.0fcm\n몸무게: %.1fkg\n섭취한 음식: %s",
                dto.height, dto.weight, dto.food);
        return ResponseEntity.ok(hamoService.simulate(prompt)).getBody();
    }

    @NoArgsConstructor
    public static class HamoUpdateDto {
        public Double height;  // 키 (cm)
        public Double weight;  // 몸무게 (kg)
        public String food;    // 섭취한 음식
    }
}
