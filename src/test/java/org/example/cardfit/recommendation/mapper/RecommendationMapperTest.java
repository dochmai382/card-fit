package org.example.cardfit.recommendation.mapper;

import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.example.cardfit.recommendation.form.CategoryMappingForm;
import org.example.cardfit.recommendation.form.ManualInputForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationMapperTest {

    private final RecommendationMapper mapper = new RecommendationMapper();

    @Test
    @DisplayName("수기 입력 폼이 엔진 표준 DTO 리스트로 정확히 변환되어야 한다")
    void shouldConvertManualInputToSummaryRequests() {
        var item1 = new ManualInputForm.ManualItem(1L, 5000L);
        var item2 = new ManualInputForm.ManualItem(2L, 100000L);
        var form = new ManualInputForm(List.of(item1, item2), 300000L);

        List<ExpenseSummaryRequest> result = mapper.toSummaryRequest(form);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("categoryId").containsExactlyInAnyOrder(1L, 2L);
        assertThat(result).extracting("monthlyAmount").containsExactlyInAnyOrder(5000L, 100000L);
    }

    @Test
    @DisplayName("엑셀 매핑 데이터 중 동일한 카테고리는 금액이 합산되어야 한다")
    void shouldAggregateAmountByCategoryForExcelMapping() {
        var item1 = new CategoryMappingForm.MappingItem("스타벅스", 5000L, 1L);
        var item2 = new CategoryMappingForm.MappingItem("서브웨이", 7000L, 1L);
        var item3 = new CategoryMappingForm.MappingItem("택시", 10000L, 2L);
        var form = new CategoryMappingForm(List.of(item1, item2, item3), 300000L);

        List<ExpenseSummaryRequest> result = mapper.toSummaryRequest(form);

        assertThat(result).hasSize(2);

        var foodSummary = result.stream()
                .filter(r -> r.categoryId().equals(1L))
                .findFirst()
                .orElseThrow();
        assertThat(foodSummary.monthlyAmount()).isEqualTo(12000L);

        var transportSummary = result.stream()
                .filter(r -> r.categoryId().equals(2L))
                .findFirst()
                .orElseThrow();
        assertThat(transportSummary.monthlyAmount()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("입력 데이터에 Null이 포함된 경우 안전하게 필터링되어야 한다")
    void shouldFilterNullValuesGracefully() {
        var validItem = new ManualInputForm.ManualItem(1L, 5000L);
        var invalidItem = new ManualInputForm.ManualItem(null, 5000L);
        var form = new ManualInputForm(List.of(validItem, invalidItem), 0L);

        List<ExpenseSummaryRequest> result = mapper.toSummaryRequest(form);

        assertThat(result).hasSize(1);
    }
}