package org.example.cardfit.recommendation.policy;

import org.example.cardfit.domain.card.Card;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScorePolicy {

    private static final int CATEGORY_MATCH_WEIGHT = 5000;

    /**
     * 카드 점수 산정
     * 혜택액 + (주요 카테고리 일치 가중치) - (연회비/12)
     * @param card 카드 정보
     * @param benefitAmount 시뮬레이션된 총 혜택액
     * @param topCategories 사용자의 주요 카테고리 ID 목록
     * @return 최종 점수
     */
    public long calculateScore(Card card, long benefitAmount, List<Long> topCategories) {
        long annualFeePenalty = calculateAnnualFeePenalty(card);
        long categoryBonus = calculateCategoryBonus(card, topCategories);

        return benefitAmount + categoryBonus - annualFeePenalty;
    }

    private long calculateAnnualFeePenalty(Card card) {
        if (card.getAnnualFee() == null) return 0;
        return card.getAnnualFee() / 12;
    }

    private long calculateCategoryBonus(Card card, List<Long> topCategories) {
        long matchCount = card.getBenefits().stream()
                .map(benefit -> benefit.getCategory().getId())
                .distinct()
                .filter(topCategories::contains)
                .count();

        return matchCount * CATEGORY_MATCH_WEIGHT;
    }
}
