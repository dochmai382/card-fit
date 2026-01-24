package org.example.cardfit.domain.benefit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {
    RATE("비율 할인"),
    AMOUNT("정액 할인")
    ;

    private final String description;
}
