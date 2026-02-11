package org.example.cardfit.recommendation.dto;

import org.example.cardfit.domain.card.Card;

import java.util.List;

public record CardRecommendation(
        Card card,
        long benefitAmount,
        long score,
        List<BenefitDetail> benefitDetails
) {
}
