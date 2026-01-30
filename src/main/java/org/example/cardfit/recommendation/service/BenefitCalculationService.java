package org.example.cardfit.recommendation.service;

import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.benefit.DiscountType;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.springframework.stereotype.Service;

@Service
public class BenefitCalculationService {

    /**
     * 단일 혜택에 대한 할인액 계산
     * @param benefit 혜택 정보
     * @param expense 지출 정보
     * @return 할인 금액 (원)
     */
    public long calculateBenefit(Benefit benefit, ExpenseSummaryRequest expense) {
        if (!isCategoryMatched(benefit, expense)) return 0L;

        if (!meetsMinPurchase(benefit, expense)) return 0L;

        long discount = calculateDiscount(benefit, expense);

        return applyMonthlyLimit(benefit, discount);
    }

    private boolean isCategoryMatched(Benefit benefit, ExpenseSummaryRequest expense) {
        return benefit.getCategory().getId().equals(expense.categoryId());
    }

    private boolean meetsMinPurchase(Benefit benefit, ExpenseSummaryRequest expense) {
        if (benefit.getMinPurchase() == null) return true;
        return expense.monthlyAmount() >= benefit.getMinPurchase();
    }

    private long calculateDiscount(Benefit benefit, ExpenseSummaryRequest expense) {
        if (benefit.getDiscountType() == DiscountType.RATE) {
            return (long) (expense.monthlyAmount() * benefit.getDiscountValue() / 100);
        } else {
            return benefit.getDiscountValue().longValue();
        }
    }

    private long applyMonthlyLimit(Benefit benefit, long discount) {
        if (benefit.getMonthlyLimit() == null) return discount;
        return Math.min(discount, benefit.getMonthlyLimit());
    }
}
