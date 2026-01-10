package com.goorm.team3.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JinjuDistrict {
    GAJWA("가좌동"),
    GANGNAM("강남동"),
    GEUMSAN("금산면"),
    DAEAN("대안동"),
    DAEGOK("대곡면"),
    DONGSONG("동성동"),
    MUNGOK("문산읍"),
    BONGGANG("봉강동"),
    BONGRAE("봉래동"),
    SANGIN("상대동"),
    SANGBONG("상봉동"),
    SANGPYEONG("상평동"),
    SINWON("신안동"),
    CHILAM("칠암동"),
    CHOJON("초전동"),
    PYEONGGO("평거동"),
    HADAE("하대동"),
    HOTAN("호탄동");

    private final String koreanName;

    public static JinjuDistrict fromKoreanName(String name) {
        for (JinjuDistrict district : values()) {
            if (district.koreanName.equals(name)) {
                return district;
            }
        }
        return null;
    }
}
