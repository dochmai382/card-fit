package org.example.cardfit.recommendation.controller;

import org.example.cardfit.infrastructure.llm.LLMClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LLMClient llmClient;

    @Test
    @DisplayName("추천 API - Top 3 카드 반환")
    void recommend_returnsTop3Cards() throws Exception {
        // given
        given(llmClient.generateExplanation(anyString())).willReturn("테스트 추천 사유입니다.");

        String requestBody = """
                {
                    "items": [
                        {"categoryId": 1, "amount": 100000},
                        {"categoryId": 2, "amount": 200000},
                        {"categoryId": 5, "amount": 150000}
                    ],
                    "expectedPerformance": 500000
                }
                """;

        // when & then
        mockMvc.perform(post("/api/recommendation/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].cardName").exists())
                .andExpect(jsonPath("$[0].issuer").exists())
                .andExpect(jsonPath("$[0].expectedBenefitAmount").exists())
                .andExpect(jsonPath("$[0].explanation").value("테스트 추천 사유입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("추천 API - 실적 미달 시 필터링")
    void recommend_filtersCardsByPerformance() throws Exception {
        // given
        given(llmClient.generateExplanation(anyString())).willReturn("추천 사유");

        String requestBody = """
                {
                    "items": [
                        {"categoryId": 1, "amount": 50000}
                    ],
                    "expectedPerformance": 100000
                }
                """;

        // when & then
        mockMvc.perform(post("/api/recommendation/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cardName").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("추천 API - 빈 지출 내역")
    void recommend_withEmptyExpenses() throws Exception {
        // given
        String requestBody = """
                {
                    "items": [],
                    "expectedPerformance": 100000
                }
                """;

        // when & then
        mockMvc.perform(post("/api/recommendation/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
