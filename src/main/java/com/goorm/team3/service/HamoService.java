package com.goorm.team3.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HamoService {

    private final WebClient anthropicWebClient; // x-api-key 헤더 포함 WebClient

    // 시스템 프롬프트
    private final String systemPrompt = """
        너는 수달 캐릭터 신체 상태 시뮬레이터이다.

        입력값(키, 몸무게, 음식, 활동)을 기반으로
        몸무게 변화와 상태 변화를 계산한다.

        출력은 반드시 JSON 한 줄만 출력한다.

        절대 하지 말 것:
        - ``` 사용 금지
        - json 코드블럭 금지
        - 설명 문장 금지
        - 개행 금지

        출력 형식은 반드시 아래 형태 그대로 유지한다.

        {"weightChange":"+0.0","newWeight":"0.0","state":""}

        숫자는 반드시 단위 없이 숫자만 출력한다.
        (+ 또는 - 기호만 허용)

        ---------------------------------
        수달 신체 지수 계산식:

        OtterIndex = weight / (height/100)^2

        ---------------------------------
        상태 구간:

        OtterIndex < 6.5 → "마른 상태"
        6.5 ≤ OtterIndex < 9 → "건강한 상태"
        9 ≤ OtterIndex < 11.5 → "통통한 상태"
        11.5 ≤ OtterIndex < 14 → "뚱뚱한 상태"
        14 ≤ OtterIndex → "몸짱 상태"

        몸무게 ≤ 0 → 죽은 상태

        ---------------------------------
        상태별 반응 규칙:

        마른 상태:
        - 음식 섭취 시 체중 증가율 크게 적용

        건강한 상태:
        - 저칼로리 음식 → 체중 감소
        - 고칼로리 음식 → 소폭 증가

        통통한 상태:
        - 대부분 음식이 소폭 증가

        뚱뚱한 상태:
        - 대부분 음식이 체중 증가

        몸짱 상태:
        - 고단백 음식은 유지 또는 소폭 증가
        - 고칼로리 음식은 증가

        ---------------------------------
        음식 해석 규칙:

        피자, 햄버거, 치킨, 라면 → 고칼로리
        샐러드, 생선, 과일 → 저칼로리

        음식 수량이 많을수록 변화량을 비례 증가 적용한다.

        ---------------------------------
        최종 출력:

        weightChange = 이번 변화량
        newWeight = 기존 몸무게 + 변화량
        state = 최종 판정 상태명
        """;

    public OtterResult simulate(String userInput) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Anthropic messages 배열 생성
            String requestBody = mapper.writeValueAsString(
                    mapper.createObjectNode()
                            .put("model", "claude-sonnet-4-5-20250929")
                            .put("max_tokens", 300)
                            .put("temperature", 0.7)
                            .set("messages", mapper.createArrayNode()
                                    .add(mapper.createObjectNode()
                                            .put("role", "user")
                                            .put("content", systemPrompt + "\n" + userInput))
                            )
            );

            // POST 요청
            String response = anthropicWebClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // response JSON에서 content 배열의 text 추출 (Messages API 형식)
            JsonNode root = mapper.readTree(response);
            String completionText = root.path("content").get(0).path("text").asText();

            return mapper.readValue(completionText, OtterResult.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class OtterResult {
        public String weightChange;
        public String newWeight;
        public String state;
    }
}