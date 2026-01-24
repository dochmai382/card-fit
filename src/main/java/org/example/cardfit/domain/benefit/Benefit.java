package org.example.cardfit.domain.benefit;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.category.Category;

@Entity
@Table(name = "card_benefits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Benefit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private Double discountValue;

    private Integer monthlyLimit;

    private Integer minPerformance;

    private Integer priority;

    private Integer minPurchase;

    @Builder
    public Benefit(Card card, Category category, DiscountType discountType, Double discountValue, Integer monthlyLimit, Integer minPerformance, Integer priority, Integer minPurchase) {
        this.card = card;
        this.category = category;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.monthlyLimit = monthlyLimit;
        this.minPerformance = minPerformance;
        this.priority = priority;
        this.minPurchase = minPurchase;
    }
}
