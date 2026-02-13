package org.example.cardfit.recommendation.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ManualInputForm(
        List<ManualItem> items,

        @Min(value = 0, message = "예상 실적은 0 이상이어야 합니다")
        Long expectedPerformance
) {
    public record ManualItem(
            @NotNull(message = "카테고리를 선택해주세요")
            Long categoryId,

            @NotNull(message = "금액을 입력해주세요")
            @Min(value = 0, message = "금액은 0 이상이어야 합니다")
            Long amount
    ){}
}
