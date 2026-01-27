package org.example.cardfit.infrastructure.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.example.cardfit.recommendation.dto.RawExpense;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PoiExcelParserTest {

    private final PoiExcelParser parser = new PoiExcelParser();

    @Test
    @DisplayName("엑셀 파일을 업로드하면 RawExpense 리스트로 정확히 변환되어야 한다")
    void shouldParserExcelFileCorrectly() throws Exception {
        // given: 가짜 엑셀 파일 (.xlsx 구조)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("날짜");
        header.createCell(1).setCellValue("가맹점명");
        header.createCell(2).setCellValue("금액");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(LocalDate.of(2026, 1, 27));
        dataRow.createCell(1).setCellValue("스타벅스 성수점");
        dataRow.createCell(2).setCellValue(5500);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // when
        List<RawExpense> result = parser.parse(mockFile);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).storeName()).isEqualTo("스타벅스 성수점");
        assertThat(result.get(0).amount()).isEqualTo(5500);
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(2026, 1, 27));
    }

}