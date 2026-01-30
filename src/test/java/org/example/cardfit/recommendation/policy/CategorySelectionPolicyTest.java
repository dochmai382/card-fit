package org.example.cardfit.recommendation.policy;

import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySelectionPolicyTest {

    private CategorySelectionPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new CategorySelectionPolicy();
    }

    @Test
    @DisplayName("10% 미만 카테고리는 제외한다")
    void excludesBelowMinRatio() {
        // given
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 500000L),
                new ExpenseSummaryRequest(2L, 450000L),
                new ExpenseSummaryRequest(3L, 50000L)
        );

        // when
        List<Long> result = policy.selectTopCategories(expenses);

        // then
        assertThat(result).containsExactly(1L, 2L);
        assertThat(result).doesNotContain(3L);
    }

    @Test
    @DisplayName("상위 3개 카테고리만 선정된다")
    void selectsTopThree() {
        // given
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 400000L),
                new ExpenseSummaryRequest(2L, 300000L),
                new ExpenseSummaryRequest(3L, 200000L),
                new ExpenseSummaryRequest(4L, 100000L)
        );

        // when
        List<Long> result = policy.selectTopCategories(expenses);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(1L, 2L, 3L);
    }
}