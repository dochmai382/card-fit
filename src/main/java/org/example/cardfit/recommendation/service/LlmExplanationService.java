package org.example.cardfit.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.cardfit.domain.category.CategoryType;
import org.example.cardfit.infrastructure.llm.LLMClient;
import org.example.cardfit.recommendation.dto.CardRecommendation;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmExplanationService {

    private final LLMClient llmClient;

    public String generateExplanation(CardRecommendation recommendation, List<ExpenseSummaryRequest> expenses) {
        String prompt = createPrompt(recommendation, expenses);
        return llmClient.generateExplanation(prompt);
    }

    private String createPrompt(CardRecommendation recommendation, List<ExpenseSummaryRequest> expenses) {
        String cardName = recommendation.card().getName();
        String issuer = recommendation.card().getIssuer();
        long benefitAmount = recommendation.benefitAmount();
        Integer annualFee = recommendation.card().getAnnualFee();

        return String.format("""
                         너는 카드 추천 전문가야. 아래 정보를 바탕으로 이 카드를 추천하는 이유를 2-3문장으로 작성해줘.
                        
                         [카드 정보]
                         - 카드명: %s (%s)
                         - 예상 혜택: 월 %,d원
                         - 연회비: %,d원
                        
                         [사용자 주요 소비]
                         %s
                        
                         [작성 규칙]
                         - 사용자의 소비 패턴과 카드 혜택을 연결해서 설명
                         - 구체적인 숫자를 활용
                         - 친근하고 설득력 있는 톤으로 작성
                         - JSON 없이 순수 텍스트로만 응답
                        """,
                cardName, issuer, benefitAmount,
                annualFee != null ? annualFee : 0,
                formatExpenses(expenses));
    }

    private String formatExpenses(List<ExpenseSummaryRequest> expenses) {
        return expenses.stream()
                .map(e -> {
                    String categoryName = Arrays.stream(CategoryType.values())
                            .filter(c -> c.getId().equals(e.categoryId()))
                            .findFirst()
                            .map(CategoryType::getName)
                            .orElse("기타");
                    return String.format("- %s: 월 %,d원", categoryName, e.monthlyAmount());
                })
                .reduce((a, b) -> a + "\n" + b)
                .orElse("없음");
    }
}
