package org.example.cardfit.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.example.cardfit.recommendation.dto.CardRecommendation;
import org.example.cardfit.recommendation.dto.ExpenseMappingResult;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.example.cardfit.recommendation.dto.RecommendationResponse;
import org.example.cardfit.recommendation.form.ExcelUploadForm;
import org.example.cardfit.recommendation.form.ManualInputForm;
import org.example.cardfit.recommendation.service.ExpenseParseService;
import org.example.cardfit.recommendation.service.LlmExplanationService;
import org.example.cardfit.recommendation.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationApiController {

    private final ExpenseParseService expenseParseService;
    private final RecommendationService recommendationService;
    private final LlmExplanationService llmExplanationService;

    @PostMapping("/upload")
    public ResponseEntity<List<ExpenseMappingResult>> uploadExcel(@ModelAttribute ExcelUploadForm form) {
        List<ExpenseMappingResult> results = expenseParseService.parseAndClassify(form.file());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<RecommendationResponse>> recommend(@RequestBody ManualInputForm form) {
        List<ExpenseSummaryRequest> expenses = form.items() == null
                ? List.of()
                : form.items().stream()
                .filter(item -> item.amount() != null && item.amount() > 0)
                .map(item -> new ExpenseSummaryRequest(item.categoryId(), item.amount()))
                .toList();

        List<CardRecommendation> recommendations = recommendationService.recommend(expenses, form.expectedPerformance());

        List<RecommendationResponse> responses = recommendations.stream()
                .map(rec -> toResponse(rec, expenses))
                .toList();

        return ResponseEntity.ok(responses);
    }

    private RecommendationResponse toResponse(CardRecommendation rec, List<ExpenseSummaryRequest> expenses) {
        String explanation = llmExplanationService.generateExplanation(rec, expenses);

        return new RecommendationResponse(
                rec.card().getName(),
                rec.card().getIssuer(),
                rec.card().getCardImageUrl(),
                rec.benefitAmount(),
                rec.benefitDetails(),
                explanation,
                rec.card().getAnnualFee()
        );
    }
}
