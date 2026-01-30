package org.example.cardfit.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.example.cardfit.recommendation.dto.ExpenseMappingResult;
import org.example.cardfit.recommendation.form.ExcelUploadForm;
import org.example.cardfit.recommendation.service.ExpenseParseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationApiController {

    private final ExpenseParseService expenseParseService;

    @PostMapping("/upload")
    public ResponseEntity<List<ExpenseMappingResult>> uploadExcel(@ModelAttribute ExcelUploadForm form) {
        List<ExpenseMappingResult> results = expenseParseService.parseAndClassify(form.file());
        return ResponseEntity.ok(results);
    }
}
