package org.example.cardfit.domain.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    COFFEE(1L, "커피/카페"),
    DINING(2L, "외식/배달"),
    TRANSPORT(3L, "대중교통/택시"),
    ONLINE_SHOPPING(4L, "온라인 쇼핑"),
    MART_CONVENIENCE(5L, "마트/편의점"),
    GAS(6L, "주유"),
    CULTURE_SUBSCRIPTION(7L, "문화/구독"),
    MEDICAL(8L, "의료/병원"),
    EDUCATION(9L, "교육/학원"),
    ETC(0L, "기타");

    private final Long id;
    private final String name;

    public static String getPromptGuide() {
        return Arrays.stream(values())
                .map(c -> c.id + ":" + c.name)
                .collect(Collectors.joining(", "));
    }
}
