package com.goorm.team3.dto;

import com.goorm.team3.domain.JinjuDistrict;
import com.goorm.team3.domain.Restaurant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantDto {

    private Long id;
    private String title;
    private String category;
    private String address;
    private String roadAddress;
    private String telephone;
    private String link;
    private String district;

    public static RestaurantDto from(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .title(cleanHtmlTags(restaurant.getTitle()))
                .category(restaurant.getCategory())
                .address(restaurant.getAddress())
                .roadAddress(restaurant.getRoadAddress())
                .telephone(restaurant.getTelephone())
                .link(restaurant.getLink())
                .district(restaurant.getDistrict() != null ? restaurant.getDistrict().getKoreanName() : null)
                .build();
    }

    private static String cleanHtmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "");
    }
}
