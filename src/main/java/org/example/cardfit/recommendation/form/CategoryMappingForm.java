package org.example.cardfit.recommendation.form;

import java.util.List;

/**
 * 엑셀 파싱 후, 사용자에게 카테고리 매핑 결과를 보여주고 수정받기 위한 폼
 */
public record CategoryMappingForm(
        List<MappingItem> items,
        Long expectedPerformance
) {
    public record MappingItem(
            String originalStoreName,
            Long amount,
            Long mappedCategoryId
    ) {}
}
