package org.example.cardfit.recommendation.dto;

import org.example.cardfit.domain.card.Card;

public record CardRecommendation(Card card, long benefitAmount, long score) {
}
