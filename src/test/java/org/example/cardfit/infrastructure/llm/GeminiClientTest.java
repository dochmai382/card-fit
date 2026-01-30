package org.example.cardfit.infrastructure.llm;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class GeminiClientTest {

    @Autowired
    private GeminiClient geminiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("실제 Gemini API가 정해진 카테고리 ID로 JSON 응답을 주는지 확인")
    @Tag("integration")
    void realApiTest() throws Exception {
        Assumptions.assumeTrue(System.getenv("GEMINI_API_KEY") != null,
                "GEMINI_API_KEY not set");

        // given
        List<String> stores = List.of("스타벅스 성수점", "배민", "GS25 강남점", "아아");

        // when
        String response = geminiClient.classify(stores);
        System.out.println("Gemini Response: " + response);

        // then
        List<Map<String, Object>> result = objectMapper.readValue(response, new TypeReference<>() {});

        assertThat(response).isNotEmpty();
        assertThat(result.get(0)).containsKey("storeName");
        assertThat(result.get(0)).containsKey("categoryId");

        Object categoryId = result.get(0).get("categoryId");
        System.out.println("Parsed CategoryId: " + categoryId);

        assertThat(categoryId.toString()).matches("\\d+");
    }

}