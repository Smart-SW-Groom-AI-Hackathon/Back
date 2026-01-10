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

    public NaverSearchResponse searchRestaurants(String district, int display) {
        // 랜덤하게 카테고리 섞기
        java.util.List<String> shuffled = new java.util.ArrayList<>(FOOD_CATEGORIES);
        java.util.Collections.shuffle(shuffled);

        java.util.List<NaverSearchResponse.Item> allItems = new java.util.ArrayList<>();
        java.util.Set<String> seenTitles = new java.util.HashSet<>();

        // 카테고리별로 검색해서 결과 합치기
        for (String category : shuffled) {
            if (allItems.size() >= display) break;

            String query = "진주시 " + district + " " + category;
            int randomStart = random.nextInt(5) + 1;

            NaverSearchResponse response = searchLocal(query, 5, randomStart);

            if (response != null && response.getItems() != null) {
                for (NaverSearchResponse.Item item : response.getItems()) {
                    String title = item.getTitle();
                    if (!seenTitles.contains(title)) {
                        seenTitles.add(title);
                        allItems.add(item);
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

        log.info("Combined search result: {} items for district {}", allItems.size(), district);
        return result;
    }
}
