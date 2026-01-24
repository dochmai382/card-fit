package org.example.cardfit.domain.card;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cardfit.domain.benefit.Benefit;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    private Integer annualFee;

    private Integer totalLimit;

    private Integer minPerformance;

    private String cardImageUrl;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Benefit> benefits = new ArrayList<>();

    @Builder
    public Card(String issuer, String name, CardType cardType, Integer annualFee, Integer totalLimit, Integer minPerformance, String cardImageUrl) {
        this.issuer = issuer;
        this.name = name;
        this.cardType = cardType;
        this.annualFee = annualFee;
        this.totalLimit = totalLimit;
        this.minPerformance = minPerformance;
        this.cardImageUrl = cardImageUrl;
    }
}
