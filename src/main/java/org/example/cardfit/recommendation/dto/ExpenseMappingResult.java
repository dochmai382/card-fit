package org.example.cardfit.recommendation.dto;

public record ExpenseMappingResult(
        String storeName,
        Long amount,
        String date,
        Long categoryId
) {
}
