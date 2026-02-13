package org.example.cardfit.infrastructure.llm;

import org.example.cardfit.domain.category.CategoryType;
import org.example.cardfit.global.error.BusinessException;
import org.example.cardfit.global.error.ErrorCode;
import org.example.cardfit.infrastructure.excel.ColumnMapping;
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

    private String  callGeminiApi(String prompt, boolean requireJsonResponse) {
        String urlWithKey = apiUrl + "?key=" + apiKey;

        Map<String, Object> requestBody;
        if (requireJsonResponse) {
            requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    ),
                    "generationConfig", Map.of(
                            "response_mime_type", "application/json"
                    )
            );
        } else {
            requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    )
            );
        }
        String rawResponse = restClient.post()
                .uri(urlWithKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractTextFromResponse(rawResponse);
    }


    @Override
    public String classify(List<String> storeNames) {
        String prompt = createPrompt(storeNames);
        return callGeminiApi(prompt, true);
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
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_ERROR);
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

    @Override
    public ColumnMapping detectColumns(List<String> headers) {
        String prompt = createHeaderDetectPrompt(headers);
        String jsonText = callGeminiApi(prompt, true);
        return parseColumnMapping(jsonText);
    }

    @Override
    public String generateExplanation(String prompt) {
        return callGeminiApi(prompt, false);
    }

    private String createHeaderDetectPrompt(List<String> headers) {
        String headerList = String.join(",", headers);

        return String.format("""
                너는 엑셀 헤더를 분석하는 데이터 처리 봇이야.
                설명은 생략하고 오직 JSON 데이터만 출력해.
                
                [분석 대상 헤더 목록]
                %s
                
                [분석 규칙]
                - dateIndex: 날짜/일자/이용일 등 날짜 관련 열의 인덱스 (0부터 시작)
                - storeNameIndex: 가맹점/상호/거래처/내역 등 가맹점명 및 내역 관련 열의 인덱스
                - amountIndex: 금액/결제/승인금액 등 금액 관련 열의 인덱스
                - typeIndex: 수입/지출 구분 열의 인덱스
                
                [출력 형식]
                {"dateIndex": 0, "storeNameIndex": 1, "amountIndex": 2, "typeIndex": 3}
                
                [주의]
                - 인덱스는 0부터 시작
                - 해당 열을 찾을 수 없으면 -1 반환
                """, headerList);
    }

    private ColumnMapping parseColumnMapping(String jsonText) {
        try {
            JsonNode node = objectMapper.readTree(jsonText);

            int dateIndex = node.path("dateIndex").asInt(-1);
            int storeNameIndex = node.path("storeNameIndex").asInt(-1);
            int amountIndex = node.path("amountIndex").asInt(-1);
            int typeIndex = node.path("typeIndex").asInt(-1);

            return new ColumnMapping(dateIndex, storeNameIndex, amountIndex, typeIndex);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_ERROR);
        }
    }
}
