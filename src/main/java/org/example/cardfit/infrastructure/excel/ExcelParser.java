package org.example.cardfit.infrastructure.excel;

import org.example.cardfit.recommendation.dto.RawExpense;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelParser {
    List<RawExpense> parse(MultipartFile file);
}
