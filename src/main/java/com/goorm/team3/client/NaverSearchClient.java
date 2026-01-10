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
        String query = "진주시 " + district + " 맛집";
        return searchLocal(query, display, 1);
    }
}
