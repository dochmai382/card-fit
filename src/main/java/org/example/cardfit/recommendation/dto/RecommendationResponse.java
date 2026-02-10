package org.example.cardfit.recommendation.dto;

import java.util.List;

public record RecommendationResponse(
        String cardName,
        String issuer,
        String cardImageUrl,
        Long expectedBenefitAmount,
        List<BenefitDetail> benefitDetails,
        String explanation,
        Integer annualFee
) {
    public record BenefitDetail(
            String categoryName,
            Integer discountAmount
    ){}
}
