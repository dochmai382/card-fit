package org.example.cardfit.recommendation.policy;

import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class CategorySelectionPolicy {

    private static final double MIN_RATIO = 0.1;
    private static final int TOP_COUNT = 3;

    /**
     * 주요 소비 카테고리 선정
     * @param expenses 지출들
     * @return 상위 3개 카테고리 ID
     */
    public List<Long> selectTopCategories(List<ExpenseSummaryRequest> expenses) {
        long totalAmount = expenses.stream()
                .mapToLong(ExpenseSummaryRequest::monthlyAmount)
                .sum();

        if (totalAmount == 0) return List.of();

        return expenses.stream()
                .filter(e -> (double) e.monthlyAmount() / totalAmount >= MIN_RATIO)
                .sorted(Comparator.comparingLong(ExpenseSummaryRequest::monthlyAmount).reversed())
                .limit(TOP_COUNT)
                .map(ExpenseSummaryRequest::categoryId)
                .toList();
    }
}
