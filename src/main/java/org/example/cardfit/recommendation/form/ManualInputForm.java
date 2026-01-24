package org.example.cardfit.recommendation.form;

import java.util.List;

public record ManualInputForm(
        List<ManualItem> items,
        Long expectedPerformance
) {
    public record ManualItem(
            Long categoryId,
            Long amount
    ){}
}
