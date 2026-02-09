package org.example.cardfit.recommendation.policy;

import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.category.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScorePolicyTest {

    private ScorePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new ScorePolicy();
    }

    @Test
    @DisplayName("점수 = 혜택 + 카테고리 가중치 - 연회비/12")
    void calculateScore_basicFormula() {
        // given
        Card card = createCardWithFeeAndBenefits(12000, List.of(1L, 2L));
        List<Long> topCategories = List.of(1L, 2L, 3L);
        long benefitAmount = 30000L;

        // when
        long score = policy.calculateScore(card, benefitAmount, topCategories);

        // then
        // 30000 + (2 * 5000) - (12000 / 12) = 30000 + 10000 - 1000 = 39000
        assertThat(score).isEqualTo(39000);
    }

    @Test
    @DisplayName("연회비가 없으면 패널티 0")
    void noAnnualFee_noPenalty() {
        // given
        Card card = createCardWithFeeAndBenefits(null, List.of(1L));
        List<Long> topCategories = List.of(1L);
        long benefitAmount = 10000L;

        // when
        long score = policy.calculateScore(card, benefitAmount, topCategories);

        // then
        // 10000 + 5000 - 0
        assertThat(score).isEqualTo(15000);
    }

    private Card createCardWithFeeAndBenefits(Integer annualFee, List<Long> categoryId) {
        Card card = mock(Card.class);
        when(card.getAnnualFee()).thenReturn(annualFee);

        List<Benefit> benefits = categoryId.stream()
                .map(this::createBenefitWithCategory)
                .toList();

        when(card.getBenefits()).thenReturn(benefits);

        return card;
    }

    private Benefit createBenefitWithCategory(Long categoryId) {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(categoryId);

        Benefit benefit = mock(Benefit.class);
        when(benefit.getCategory()).thenReturn(category);

        return benefit;
    }
}