package org.example.cardfit.infrastructure.excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.example.cardfit.recommendation.dto.RawExpense;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PoiExcelParser implements ExcelParser{

    private final HeaderDetector headerDetector;

    @Override
    public List<RawExpense> parse(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            List<String> headers = extractHeaders(headerRow);

            ColumnMapping mapping = headerDetector.detect(headers);

            List<RawExpense> expenses = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                LocalDate date = getLocalDate(row.getCell(mapping.dateIndex()));
                String storeName = getStringValue(row.getCell(mapping.storeNameIndex()));
                long amount = (long) getNumericValue(row.getCell(mapping.amountIndex()));

                expenses.add(new RawExpense(date, storeName, amount));
            }
            return expenses;

        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 파싱 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
     }

    private LocalDate getLocalDate(Cell cell) {
        if (cell == null) return LocalDate.now();
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.now();
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : "";
    }

    private double getNumericValue(Cell cell) {
        if (cell == null) return 0.0;
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0.0;
    }

    private List<String> extractHeaders(Row row) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            headers.add(getStringValue(row.getCell(i)));
        }
        return headers;
    }
}
