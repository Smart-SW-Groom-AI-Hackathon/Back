package com.goorm.team3.client;

import com.goorm.team3.client.dto.NaverSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverSearchClient {

    private final WebClient naverWebClient;
    private final java.util.Random random = new java.util.Random();

    private static final java.util.List<String> FOOD_CATEGORIES = java.util.List.of(
            "한식", "일식", "중식", "양식", "분식", "치킨", "피자", "햄버거",
            "카페", "베이커리", "고기", "해물", "국밥", "냉면", "찌개"
    );

    public NaverSearchResponse searchLocal(String query, int display, int start) {
        log.info("Searching Naver Local API: query={}, display={}, start={}", query, display, start);

        return naverWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/local.json")
                        .queryParam("query", query)
                        .queryParam("display", display)
                        .queryParam("start", start)
                        .queryParam("sort", "random")
                        .build())
                .retrieve()
                .bodyToMono(NaverSearchResponse.class)
                .block();
    }

    public NaverSearchResponse searchRestaurants(String district, String category, int display) {
        java.util.List<String> categoriesToSearch;

        // category가 지정되면 해당 카테고리만, 아니면 모든 카테고리 검색
        if (category != null && !category.isEmpty()) {
            categoriesToSearch = java.util.List.of(category);
        } else {
            categoriesToSearch = new java.util.ArrayList<>(FOOD_CATEGORIES);
            java.util.Collections.shuffle(categoriesToSearch);
        }

        java.util.List<NaverSearchResponse.Item> allItems = new java.util.ArrayList<>();

        // 카테고리별로 검색해서 결과 합치기
        for (String cat : categoriesToSearch) {
            if (allItems.size() >= display) break;

            String query = "진주시 " + district + " " + cat;
            int randomStart = random.nextInt(5) + 1;

            NaverSearchResponse response = searchLocal(query, 5, randomStart);

            if (response != null && response.getItems() != null) {
                for (NaverSearchResponse.Item item : response.getItems()) {
                    allItems.add(item);
                    if (allItems.size() >= display) break;
                }
            }
        }

        // 결과가 부족하면 추가 검색
        if (allItems.size() < display) {
            log.warn("Not enough results, searching with more offsets");

            // category가 지정된 경우 해당 category로만, 아니면 모든 카테고리로 추가 검색
            java.util.List<String> additionalCategories;
            if (category != null && !category.isEmpty()) {
                additionalCategories = java.util.List.of(category);
            } else {
                additionalCategories = new java.util.ArrayList<>(FOOD_CATEGORIES);
                java.util.Collections.shuffle(additionalCategories);
            }

            for (String cat : additionalCategories) {
                if (allItems.size() >= display) break;

                String query = "진주시 " + district + " " + cat;

                // 여러 start 값으로 재시도
                for (int startOffset = 1; startOffset <= 10 && allItems.size() < display; startOffset++) {
                    NaverSearchResponse response = searchLocal(query, 10, startOffset);

                    if (response != null && response.getItems() != null) {
                        for (NaverSearchResponse.Item item : response.getItems()) {
                            allItems.add(item);
                            if (allItems.size() >= display) break;
                        }
                    }
                }
            }
        }

        // 결과를 NaverSearchResponse로 만들어서 반환
        NaverSearchResponse result = new NaverSearchResponse();
        result.setItems(allItems);
        result.setTotal(allItems.size());
        result.setDisplay(allItems.size());
        result.setStart(1);

        log.info("Combined search result: {} items for district {} with category {}",
                allItems.size(), district, category != null ? category : "all");
        return result;
    }
}
