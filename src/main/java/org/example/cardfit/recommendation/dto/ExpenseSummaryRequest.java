package org.example.cardfit.recommendation.dto;

public record ExpenseSummaryRequest(
        Long categoryId,
        Long monthlyAmount
) {
}
