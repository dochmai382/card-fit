package org.example.cardfit.recommendation.service;

import org.example.cardfit.domain.category.CategoryType;
import org.example.cardfit.infrastructure.excel.PoiExcelParser;
import org.example.cardfit.infrastructure.llm.LLMClient;
import org.example.cardfit.recommendation.dto.ExpenseMappingResult;
import org.example.cardfit.recommendation.dto.RawExpense;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ExpenseParseServiceTest {

    @MockitoBean
    private PoiExcelParser excelParser;

    @MockitoBean
    private LLMClient llmClient;

    @Autowired
    private ExpenseParseService expenseParseService;

    @Test
    @DisplayName("엑셀 파일을 업로드하면 AI가 카테고리를 매핑하여 결과를 반환한다")
    void parseAndClassifyTest() {
        // given
        List<RawExpense> mockRawExpenses = List.of(
                new RawExpense(LocalDate.of(2026, 01, 27), "스타벅스", 5000L),
                new RawExpense(LocalDate.of(2026, 01, 27), "배달의민족", 25000L)
        );
        given(excelParser.parse(any())).willReturn(mockRawExpenses);
        given(llmClient.classify(any())).willReturn(
                "[{\"storeName\":\"스타벅스\",\"categoryId\":1},{\"storeName\":\"배달의민족\",\"categoryId\":2}]"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "text/plain",
                "dummy".getBytes()
        );

        // when
        List<ExpenseMappingResult> results = expenseParseService.parseAndClassify(file);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).categoryId()).isEqualTo(1L);
    }

}