package org.example.cardfit.recommendation.service;

import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.benefit.DiscountType;
import org.example.cardfit.domain.category.Category;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BenefitCalculationServiceTest {

    private BenefitCalculationService service;

    @BeforeEach
    void setUp() {
        service = new BenefitCalculationService();
    }

    private Category createCategory(Long id) {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(id);
        return category;
    }

    @Test
    @DisplayName("카테고리 불일치 시 할인 0원")
    void categoryMismatch_returnsZero() {
        // given
        Category category = createCategory(1L);
        Benefit benefit = Benefit.builder()
                .category(category)
                .discountType(DiscountType.RATE)
                .discountValue(10.0)
                .build();
        ExpenseSummaryRequest expense = new ExpenseSummaryRequest(2L, 100000L);

        // when
        long discount = service.calculateBenefit(benefit, expense);

        // then
        assertThat(discount).isEqualTo(0);
    }

    @Test
    @DisplayName("건당 최소 결제금액 미달 시 할인 0원")
    void minPurchaseNotMet_returnsZero() {
        // given
        Category category = createCategory(1L);
        Benefit benefit = Benefit.builder()
                .category(category)
                .discountType(DiscountType.RATE)
                .discountValue(10.0)
                .minPurchase(50000)
                .build();
        ExpenseSummaryRequest expense = new ExpenseSummaryRequest(1L, 30000L);

        // when
        long discount = service.calculateBenefit(benefit, expense);

        // then
        assertThat(discount).isEqualTo(0);
    }

    @Test
    @DisplayName("비율 할인이 정상 계산")
    void rateDiscount_calculatesCorrectly() {
        // given
        Category category = createCategory(1L);
        Benefit benefit = Benefit.builder()
                .category(category)
                .discountType(DiscountType.RATE)
                .discountValue(10.0)
                .build();
        ExpenseSummaryRequest expense = new ExpenseSummaryRequest(1L, 30000L);

        // when
        long discount = service.calculateBenefit(benefit, expense);

        // then
        assertThat(discount).isEqualTo(3000);
    }

    @Test
    @DisplayName("개별 혜택 월 한도 초과 시 절사")
    void monthlyLimit_capsDiscount() {
        // given
        Category category = createCategory(1L);
        Benefit benefit = Benefit.builder()
                .category(category)
                .discountType(DiscountType.RATE)
                .discountValue(10.0)
                .monthlyLimit(5000)
                .build();
        ExpenseSummaryRequest expense = new ExpenseSummaryRequest(1L, 100000L);

        // when
        long discount = service.calculateBenefit(benefit, expense);

        // then
        assertThat(discount).isEqualTo(5000);
    }
}