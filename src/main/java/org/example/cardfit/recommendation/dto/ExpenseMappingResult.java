package org.example.cardfit.recommendation.dto;

import org.example.cardfit.domain.category.CategoryType;

public record ExpenseMappingResult(
        String storeName,
        Long amount,
        String date,
        CategoryType categoryType
) {
}
