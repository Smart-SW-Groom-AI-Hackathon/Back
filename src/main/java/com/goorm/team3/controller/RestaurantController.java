package com.goorm.team3.controller;

import com.goorm.team3.domain.JinjuDistrict;
import com.goorm.team3.dto.RestaurantDto;
import com.goorm.team3.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/districts")
    public ResponseEntity<List<String>> getDistricts() {
        return ResponseEntity.ok(restaurantService.getAllDistricts());
    }

    @GetMapping("/restaurants/search")
    public ResponseEntity<List<RestaurantDto>> searchRestaurants(
            @RequestParam String district,
            @RequestParam(defaultValue = "10") int count) {

        JinjuDistrict jinjuDistrict = JinjuDistrict.fromKoreanName(district);
        if (jinjuDistrict == null) {
            return ResponseEntity.badRequest().build();
        }

        List<RestaurantDto> restaurants = restaurantService.searchAndSave(jinjuDistrict, count);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsByDistrict(
            @RequestParam String district) {

        JinjuDistrict jinjuDistrict = JinjuDistrict.fromKoreanName(district);
        if (jinjuDistrict == null) {
            return ResponseEntity.badRequest().build();
        }

        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByDistrict(jinjuDistrict);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/restaurants/random")
    public ResponseEntity<RestaurantDto> getRandomRestaurant(
            @RequestParam String district) {

        JinjuDistrict jinjuDistrict = JinjuDistrict.fromKoreanName(district);
        if (jinjuDistrict == null) {
            return ResponseEntity.badRequest().build();
        }

        RestaurantDto restaurant = restaurantService.getRandomRestaurant(jinjuDistrict);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id) {
        RestaurantDto restaurant = restaurantService.getRestaurantById(id);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(restaurant);
    }
}
