package com.goorm.team3.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NaverSearchResponse {

    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        private String title;
        private String link;
        private String category;
        private String description;
        private String telephone;
        private String address;
        private String roadAddress;
        private int mapx;
        private int mapy;
    }
}
