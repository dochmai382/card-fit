package org.example.cardfit.recommendation.dto;

import java.time.LocalDate;

public record RawExpense(
        LocalDate date,
        String storeName,
        Long amount
) {
}
