package org.example.cardfit.recommendation.controller;

import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.card.CardType;
import org.example.cardfit.recommendation.dto.CardRecommendation;
import org.example.cardfit.recommendation.service.ExpenseParseService;
import org.example.cardfit.recommendation.service.LlmExplanationService;
import org.example.cardfit.recommendation.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecommendationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseParseService expenseParseService;

    @MockitoBean
    private RecommendationService recommendationService;

    @MockitoBean
    private LlmExplanationService llmExplanationService;

    @Test
    @DisplayName("추천 API 호출 시 TOP 3 카드 반환")
    void recommend_returnsTop3Cards() throws Exception {
        // given
        Card mockCard = Card.builder()
                .name("테스트 카드")
                .issuer("테스트은행")
                .cardType(CardType.CREDIT)
                .annualFee(10000)
                .cardImageUrl("https://example.com/card.png")
                .build();

        CardRecommendation mockRecommendation = new CardRecommendation(mockCard, 5000L, 100L);

        given(recommendationService.recommend(any(), any())).willReturn(List.of(mockRecommendation));
        given(llmExplanationService.generateExplanation(any(), any())).willReturn("이 카드는 커피 할인이 좋습니다.");

        String requestBody = """
                {
                    "items": [
                        {"categoryId": 1, "amount": 100000},
                        {"categoryId": 2, "amount": 200000}
                    ],
                    "expectedPerformance": 300000
                }
                """;

        // when & then
        mockMvc.perform(post("/api/recommendation/recommend")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardName").value("테스트 카드"))
                .andExpect(jsonPath("$[0].issuer").value("테스트은행"))
                .andExpect(jsonPath("$[0].expectedBenefitAmount").value(5000))
                .andExpect(jsonPath("$[0].explanation").value("이 카드는 커피 할인이 좋습니다."));
    }

}