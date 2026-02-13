package org.example.cardfit.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.card.CardRepository;
import org.example.cardfit.domain.card.CardStatus;
import org.example.cardfit.domain.card.CardType;
import org.example.cardfit.domain.category.CategoryType;
import org.example.cardfit.recommendation.dto.BenefitDetail;
import org.example.cardfit.recommendation.dto.CardRecommendation;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.example.cardfit.recommendation.policy.CategorySelectionPolicy;
import org.example.cardfit.recommendation.policy.ScorePolicy;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final CardRepository cardRepository;
    private final BenefitCalculationService benefitCalculationService;
    private final CategorySelectionPolicy categorySelectionPolicy;
    private final ScorePolicy scorePolicy;

    private static final int TOP_COUNT = 3;

    private static final Comparator<CardRecommendation> RECOMMENDATION_COMPARATOR = Comparator
            .comparingLong(CardRecommendation::score).reversed()
            .thenComparing(Comparator.comparingLong(CardRecommendation::benefitAmount).reversed())
            .thenComparingInt(r -> r.card().getAnnualFee() != null ? r.card().getAnnualFee() : 0)
            .thenComparing(r -> r.card().getCardType() == CardType.CHECK ? 0 : 1);


    public List<CardRecommendation> recommend(List<ExpenseSummaryRequest> expenses, Long userPerformance) {
        log.info("카드 추천 요청: 카테고리 {}개, 예상실적 {}", expenses.size(), userPerformance);

        List<Long> topCategories = categorySelectionPolicy.selectTopCategories(expenses);
        List<Card> activeCards = cardRepository.findByStatus(CardStatus.ACTIVE);

        List<CardRecommendation> result = activeCards.stream()
                .filter(card -> meetsPerformance(card, userPerformance))
                .map(card -> calculateCardScore(card, expenses, topCategories))
                .sorted(RECOMMENDATION_COMPARATOR)
                .limit(TOP_COUNT)
                .toList();

        log.info("카드 추천 완료: {}개 추천", result.size());
        return result;
    }

    private boolean meetsPerformance(Card card, Long userPerformance) {
        if (card.getMinPerformance() == null) return true;
        if (userPerformance == null) return false;
        return userPerformance >= card.getMinPerformance();
    }

    private CardRecommendation calculateCardScore(Card card, List<ExpenseSummaryRequest> expenses, List<Long> topCategories) {
        long remainingLimit = card.getTotalLimit() != null ? card.getTotalLimit() : Long.MAX_VALUE;

        List<Benefit> sortedBenefits = card.getBenefits().stream()
                .sorted(Comparator.comparingInt(b -> b.getPriority() != null ? b.getPriority() : Integer.MAX_VALUE))
                .toList();

        long totalBenefit = 0;
        Map<Long, Long> categoryBenefits = new HashMap<>();

        for (Benefit benefit : sortedBenefits) {
            if (remainingLimit <= 0) break;

            for (ExpenseSummaryRequest expense : expenses) {
                if (remainingLimit <= 0) break;

                long benefitAmount = benefitCalculationService.calculateBenefit(benefit, expense);
                long appliedAmount = Math.min(benefitAmount, remainingLimit);
                totalBenefit += appliedAmount;
                remainingLimit -= appliedAmount;

                categoryBenefits.merge(expense.categoryId(), appliedAmount, Long::sum);
            }
        }

        List<BenefitDetail> benefitDetails = categoryBenefits.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> {
                    String categoryName = Arrays.stream(CategoryType.values())
                            .filter(c -> c.getId().equals(e.getKey()))
                            .findFirst()
                            .map(CategoryType::getName)
                            .orElse("기타");
                    return new BenefitDetail(categoryName, e.getValue());
                })
                .toList();

        long score = scorePolicy.calculateScore(card, totalBenefit, topCategories);

        return new CardRecommendation(card, totalBenefit, score, benefitDetails);
    }
}
