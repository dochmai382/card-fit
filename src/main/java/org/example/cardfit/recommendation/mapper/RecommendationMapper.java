package org.example.cardfit.recommendation.mapper;

import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.example.cardfit.recommendation.form.CategoryMappingForm;
import org.example.cardfit.recommendation.form.ManualInputForm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RecommendationMapper {

    /**
     * 수기 입력 폼 -> 엔진 표준 입력 모델 변환
     */
    public List<ExpenseSummaryRequest> toSummaryRequest(ManualInputForm form) {
        if (form == null || form.items() == null) return List.of();

        return form.items().stream()
                .filter(item -> item.categoryId() != null && item.amount() != null)
                .map(item -> new ExpenseSummaryRequest(item.categoryId(), item.amount()))
                .collect(Collectors.toList());
    }

    /**
     * 엑셀 매핑 확정 폼 -> 엔진 표준 입력 모델 변환
     */
    public List<ExpenseSummaryRequest> toSummaryRequest(CategoryMappingForm form) {
        if (form == null || form.items() == null) return List.of();

        Map<Long, Long> sumByCategory = form.items().stream()
                .filter(item -> item.mappedCategoryId() != null && item.amount() != null)
                .collect(Collectors.groupingBy(
                        CategoryMappingForm.MappingItem::mappedCategoryId,
                        Collectors.summingLong(CategoryMappingForm.MappingItem::amount)
                ));

        return sumByCategory.entrySet().stream()
                .map(entry -> new ExpenseSummaryRequest(entry.getKey(), entry.getValue()))
                .toList();
    }

}
