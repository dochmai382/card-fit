package org.example.cardfit.recommendation.service;

import org.assertj.core.api.Assertions;
import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.card.CardType;
import org.example.cardfit.infrastructure.llm.LLMClient;
import org.example.cardfit.recommendation.dto.CardRecommendation;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.example.cardfit.recommendation.service.RecommendationService.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LlmExplanationServiceTest {

    @Mock
    private LLMClient llmClient;

    @InjectMocks
    private LlmExplanationService llmExplanationService;

    @Test
    @DisplayName("추천 사유 생성 시 LLM 호출")
    void generateExplanation_callsLlmClient() {
        // given
        Card card = Card.builder()
                .name("신한카드 Mr.Life")
                .issuer("신한카드")
                .cardType(CardType.CREDIT)
                .annualFee(15000)
                .build();

        CardRecommendation recommendation = new CardRecommendation(card, 10000L, 100L, List.of());

        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L),
                new ExpenseSummaryRequest(2L, 200000L)
        );

        given(llmClient.generateExplanation(anyString())).willReturn("커피와 외식 소비가 많은 당신에게 딱 맞는 카드입니다.");

        // when
        String result = llmExplanationService.generateExplanation(recommendation, expenses);

        // then
        assertThat(result).isEqualTo("커피와 외식 소비가 많은 당신에게 딱 맞는 카드입니다.");
        verify(llmClient).generateExplanation(anyString());
    }

    @Test
    @DisplayName("프롬프트에 카드 정보와 소비 내역 포함")
    void generateExplanation_promptContainsCardInfoAndExpenses() {
        // given
        Card card = Card.builder()
                .name("신한카드 Mr.Life")
                .issuer("신한카드")
                .cardType(CardType.CREDIT)
                .annualFee(15000)
                .build();

        CardRecommendation recommendation = new CardRecommendation(card, 10000L, 100L, List.of());

        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L)
        );

        given(llmClient.generateExplanation(anyString())).willReturn("추천 사유");

        // when
        llmExplanationService.generateExplanation(recommendation, expenses);

        // then
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);

        verify(llmClient).generateExplanation(promptCaptor.capture());

        String prompt = promptCaptor.getValue();
        assertThat(prompt).contains("신한카드 Mr.Life");
        assertThat(prompt).contains("신한카드");
        assertThat(prompt).contains("10,000");
        assertThat(prompt).contains("커피/카페");
    }
}