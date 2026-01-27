package org.example.cardfit.infrastructure.excel;

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
public class PoiExcelParser implements ExcelParser{
    @Override
    public List<RawExpense> parse(MultipartFile file) {
        List<RawExpense> expenses = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                LocalDate date = getLocalDate(row.getCell((0)));
                String storeName = getStringValue(row.getCell(1));
                long amount = (long) getNumericValue(row.getCell(2));

                expenses.add(new RawExpense(date, storeName, amount));
            }
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 파싱 중 오류가 발생했습니다: " + e.getMessage());
        }

        return expenses;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
     }

    private LocalDate getLocalDate(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.now();
    }

    private String getStringValue(Cell cell) {
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : "";
    }

    private double getNumericValue(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0.0;
    }
}
