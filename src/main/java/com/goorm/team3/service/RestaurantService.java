package com.goorm.team3.service;

import com.goorm.team3.client.NaverSearchClient;
import com.goorm.team3.client.dto.NaverSearchResponse;
import com.goorm.team3.domain.JinjuDistrict;
import com.goorm.team3.domain.Restaurant;
import com.goorm.team3.dto.RestaurantDto;
import com.goorm.team3.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final NaverSearchClient naverSearchClient;
    private final RestaurantRepository restaurantRepository;
    private final Random random = new Random();

    @Transactional
    public List<RestaurantDto> searchAndSave(JinjuDistrict district, String category, int count) {
        NaverSearchResponse response = naverSearchClient.searchRestaurants(
                district.getKoreanName(), category, count);

        if (response == null || response.getItems() == null) {
            log.warn("No results from Naver API for district: {} with category: {}", district, category);
            return Collections.emptyList();
        }

        List<Restaurant> restaurants = response.getItems().stream()
                .filter(item -> !restaurantRepository.existsByTitleAndAddress(
                        cleanHtmlTags(item.getTitle()), item.getAddress()))
                .map(item -> convertToEntity(item, district))
                .collect(Collectors.toList());

        List<Restaurant> saved = restaurantRepository.saveAll(restaurants);
        log.info("Saved {} new restaurants for district: {} with category: {}", saved.size(), district, category);

        return saved.stream()
                .map(RestaurantDto::from)
                .collect(Collectors.toList());
    }

    public List<RestaurantDto> getRestaurantsByDistrict(JinjuDistrict district, String category) {
        List<Restaurant> restaurants;
        if (category != null && !category.isEmpty()) {
            restaurants = restaurantRepository.findByDistrictAndCategory(district.name(), category);
        } else {
            restaurants = restaurantRepository.findByDistrict(district);
        }

        // DB에 데이터가 없으면 API에서 가져와서 저장
        if (restaurants.isEmpty()) {
            log.info("No restaurants in DB, fetching from API");
            return searchAndSave(district, category, 10);
        }

        return restaurants.stream()
                .map(RestaurantDto::from)
                .collect(Collectors.toList());
    }

    public RestaurantDto getRandomRestaurant(JinjuDistrict district, String category) {
        List<Restaurant> restaurants;
        if (category != null && !category.isEmpty()) {
            restaurants = restaurantRepository.findRandomByDistrictAndCategory(district.name(), category);
        } else {
            restaurants = restaurantRepository.findRandomByDistrict(district.name());
        }

        // DB에 데이터가 없으면 API에서 가져와서 저장 후 랜덤 반환
        if (restaurants.isEmpty()) {
            log.info("No restaurants in DB for random selection, fetching from API");
            List<RestaurantDto> newRestaurants = searchAndSave(district, category, 10);
            if (!newRestaurants.isEmpty()) {
                int randomIndex = random.nextInt(newRestaurants.size());
                return newRestaurants.get(randomIndex);
            }
            return null;
        }

        Restaurant randomRestaurant = restaurants.get(0); // findRandom... already shuffles
        return RestaurantDto.from(randomRestaurant);
    }
    
    public List<String> getCategoriesByDistrict(JinjuDistrict district) {
        return restaurantRepository.findCategoriesByDistrict(district);
    }

    public RestaurantDto getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(RestaurantDto::from)
                .orElse(null);
    }

    public List<String> getAllDistricts() {
        return java.util.Arrays.stream(JinjuDistrict.values())
                .map(JinjuDistrict::getKoreanName)
                .collect(Collectors.toList());
    }

    private Restaurant convertToEntity(NaverSearchResponse.Item item, JinjuDistrict district) {
        return Restaurant.builder()
                .title(cleanHtmlTags(item.getTitle()))
                .category(item.getCategory())
                .address(item.getAddress())
                .roadAddress(item.getRoadAddress())
                .telephone(item.getTelephone())
                .link(item.getLink())
                .district(district)
                .mapx(item.getMapx())
                .mapy(item.getMapy())
                .build();
    }

    private String cleanHtmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "");
    }
}
