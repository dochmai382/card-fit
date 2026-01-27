package org.example.cardfit.infrastructure.llm;

import org.example.cardfit.domain.category.CategoryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeType;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient implements LLMClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiClient(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    @Override
    public String classify(List<String> storeNames) {
        String prompt = createPrompt(storeNames);
        String urlWithKey = apiUrl + "?key=" + apiKey;
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json"
                )
        );
        String rawResponse = restClient.post()
                .uri(urlWithKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractTextFromResponse(rawResponse);
    }

    private String extractTextFromResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(cleanJsonText(rawResponse));
            JsonNode textNode = root.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text");

            if (textNode.getNodeType() == JsonNodeType.STRING) {
                return textNode.asString();
            }

            return "";
        } catch (Exception e) {
            throw new RuntimeException("Gemini API 응답 파싱 실패");
        }
    }

    private String cleanJsonText(String rawText) {
        return rawText.replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private String createPrompt(List<String> storeNames) {
        String stores = String.join(", ", storeNames);
        String categoryGuide = CategoryType.getPromptGuide();

        return String.format("""
                너는 지출 내역을 숫자로만 분류하는 데이터 처리 봇이야.
                설명은 생략하고 오직 JSON 데이터만 출력해.
                
                [분류 규칙 (ID:이름)]
                %s
                
                [출력 형식]
                - 반드시 categoryId 필드에 숫자를 넣어야 함.
                - 가맹점 및 내역은 원본 그대로 유지할 것.
                - 카테고리가 모호하면 기타(0)로 분류할 것.
                
                [출력 예시]
                [{"storeName": "스타벅스", "categoryId": 1}]
                
                [대상 가맹점 및 내역 리스트]
                %s
                
                """, categoryGuide, stores);
    }
}
