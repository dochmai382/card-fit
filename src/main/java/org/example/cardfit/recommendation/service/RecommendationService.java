package org.example.cardfit.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.card.CardRepository;
import org.example.cardfit.domain.card.CardStatus;
import org.example.cardfit.domain.card.CardType;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.example.cardfit.recommendation.policy.CategorySelectionPolicy;
import org.example.cardfit.recommendation.policy.ScorePolicy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final CardRepository cardRepository;
    private final BenefitCalculationService benefitCalculationService;
    private final CategorySelectionPolicy categorySelectionPolicy;
    private final ScorePolicy scorePolicy;

    private static final int TOP_COUNT = 3;

    public List<CardRecommendation> recommend(List<ExpenseSummaryRequest> expenses, Long userPerformance) {
        List<Long> topCategories = categorySelectionPolicy.selectTopCategories(expenses);
        List<Card> activeCards = cardRepository.findByStatus(CardStatus.ACTIVE);

        return activeCards.stream()
                .filter(card -> meetsPerformance(card, userPerformance))
                .map(card -> calculateCardScore(card, expenses, topCategories))
                .sorted(getRecommendationComparator())
                .limit(TOP_COUNT)
                .toList();
    }

    private boolean meetsPerformance(Card card, Long userPerformance) {
        if (card.getMinPerformance() == null) return true;
        return userPerformance >= card.getMinPerformance();
    }

    private CardRecommendation calculateCardScore(Card card, List<ExpenseSummaryRequest> expenses, List<Long> topCategories) {
        long remainingLimit = card.getTotalLimit() != null ? card.getTotalLimit() : Long.MAX_VALUE;

        List<Benefit> sortedBenefits = card.getBenefits().stream()
                .sorted(Comparator.comparingInt(b -> b.getPriority() != null ? b.getPriority() : Integer.MAX_VALUE))
                .toList();

        long totalBenefit = 0;
        for (Benefit benefit : sortedBenefits) {
            if (remainingLimit <= 0) break;

            long benefitAmount = calculateBenefitForAllExpenses(benefit, expenses);

            long appliedAmount = Math.min(benefitAmount, remainingLimit);
            totalBenefit += appliedAmount;
            remainingLimit -= appliedAmount;
        }

        long score = scorePolicy.calculateScore(card, totalBenefit, topCategories);

        return new CardRecommendation(card, totalBenefit, score);
    }

    private long calculateBenefitForAllExpenses(Benefit benefit, List<ExpenseSummaryRequest> expenses) {
        return expenses.stream()
                .mapToLong(expense -> benefitCalculationService.calculateBenefit(benefit, expense))
                .sum();
    }

    private Comparator<CardRecommendation> getRecommendationComparator() {
        return Comparator
                .comparingLong(CardRecommendation::score).reversed()
                .thenComparing(Comparator.comparingLong(CardRecommendation::benefitAmount).reversed())
                .thenComparingInt(r -> r.card().getAnnualFee() != null ? r.card().getAnnualFee() : 0)
                .thenComparing(r -> r.card().getCardType() == CardType.CHECK ? 0 : 1);
    }

    public record CardRecommendation(Card card, long benefitAmount, long score){}
}
