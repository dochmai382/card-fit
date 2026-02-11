package org.example.cardfit.recommendation.service;

import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.benefit.DiscountType;
import org.example.cardfit.domain.card.Card;
import org.example.cardfit.domain.card.CardRepository;
import org.example.cardfit.domain.card.CardStatus;
import org.example.cardfit.domain.card.CardType;
import org.example.cardfit.domain.category.Category;
import org.example.cardfit.recommendation.dto.BenefitDetail;
import org.example.cardfit.recommendation.dto.ExpenseSummaryRequest;
import org.example.cardfit.recommendation.policy.CategorySelectionPolicy;
import org.example.cardfit.recommendation.policy.ScorePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationServiceTest {

    @Mock
    private CardRepository cardRepository;

    private RecommendationService recommendationService;
    private BenefitCalculationService benefitCalculationService;
    private CategorySelectionPolicy categorySelectionPolicy;
    private ScorePolicy scorePolicy;

    @BeforeEach
    void setUp() {
        benefitCalculationService = new BenefitCalculationService();
        categorySelectionPolicy = new CategorySelectionPolicy();
        scorePolicy = new ScorePolicy();
        recommendationService = new RecommendationService(
                cardRepository,
                benefitCalculationService,
                categorySelectionPolicy,
                scorePolicy
        );
    }

    @Test
    @DisplayName("시나리오1: 통합 할인 한도 초과 시 정확히 절사된다")
    void totalLimit_capsDiscount() {
        // given
        Category category = createCategory(1L);
        // 카드: 통합 한도 10,000원
        Card card = createCard(10000, null, List.of(createBenefit(category, DiscountType.RATE, 10.0, null, 1))); // 10% 할인
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(card));
        // 지출: 20만원 -> 10% = 2만원 할인 예상
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 200000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).benefitAmount()).isEqualTo(10000); // 한도 적용
    }

    @Test
    @DisplayName("시나리오2: 전월 실적 미달 카드가 필터링된다")
    void performanceNotMet_cardFiltered() {
        // given
        Category category = createCategory(1L);
        // 카드: 최소 실적 50만원 요구
        Card card = createCard(null, 500000, List.of(createBenefit(category, DiscountType.RATE, 10.0, null, 1)));
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(card));
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L)
        );

        // when: 사용자 실적 30만원 (미달)
        var results = recommendationService.recommend(expenses, 300000L);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("시나리오3: 우선순위가 높은 혜택이 한도를 선점한다")
    void priority_higherBenefitGetsLimitFirst() {
        // given
        Category category1 = createCategory(1L);
        Category category2 = createCategory(2L);

        // 혜택1: 10%할인, 한도 없음, priority 1 -> 10만원의 10% -> 10,000원
        // 혜택2: 10%할인, 한도 없음, priority 2 -> 10만원의 10% -> 10,000원
        // 통합 한도: 12,000원
        Card card = createCard(12000, null, List.of(
                createBenefit(category1, DiscountType.RATE, 10.0, null, 1),
                createBenefit(category2, DiscountType.RATE, 10.0, null, 2)
        ));
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(card));
        // 지출: 카테고리1 10만원, 카테고리2 10만원 -> 각각 1만원씩 2만원
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L),
                new ExpenseSummaryRequest(2L, 100000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        // 점진적 차감: 헤택1(10,000) 전액 + 혜택2(2,000) = 12,000
        assertThat(results.get(0).benefitAmount()).isEqualTo(12000);
    }

    @Test
    @DisplayName("시나리오4: 건당 최소 결제금액 미달시 해당 혜택 제외")
    void minPurchase_excludeBenefit() {
        // given
        Category category = createCategory(1L);
        // 카드: 최소 결제금액 50,000원
        Card card = createCard(null, null, List.of(
                createBenefit(category, DiscountType.RATE, 10.0, 50000, 1)
        ));
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(card));
        // 지출: 3만원 (최소 금액 미달)
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 30000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        assertThat(results.get(0).benefitAmount()).isZero();
    }

    @Test
    @DisplayName("TOP3 카드가 점수 순으로 정렬된다")
    void recommend_returnsTop3ByScore() {
        // given
        Category category = createCategory(1L);
        // 카드 3개
        Card cardA = createCardWithName("카드A", null, null, 0, CardType.CREDIT, List.of(createBenefit(category, DiscountType.RATE, 5.0, null, 1))); // 5%
        Card cardB = createCardWithName("카드B", null, null, 0, CardType.CREDIT, List.of(createBenefit(category, DiscountType.RATE, 10.0, null, 1))); // 10%
        Card cardC = createCardWithName("카드C", null, null, 0, CardType.CREDIT, List.of(createBenefit(category, DiscountType.RATE, 15.0, null, 1))); // 15%
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(cardA, cardB, cardC));

        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        assertThat(results).hasSize(3);
        assertThat(results.get(0).benefitAmount()).isEqualTo(15000); // 카드C
        assertThat(results.get(1).benefitAmount()).isEqualTo(10000); // 카드B
        assertThat(results.get(2).benefitAmount()).isEqualTo(5000); // 카드A
    }

    @Test
    @DisplayName("동점 시 실익 -> 연회비 -> 카드타입 순으로 정렬된다")
    void recommend_tieBreaker() {
        // given
        Category category = createCategory(1L);
        // 모든 카드 같은 혜택 (동점)
        // 카드A: 연회비 10,000, 신용카드
        // 카드B: 연회비 5,000, 신용카드
        // 카드C: 연회비 5,000, 체크카드
        Card cardA = createCardWithName("카드A", null, null, 10000, CardType.CREDIT, List.of(createBenefit(category, DiscountType.RATE, 10.0, null, 1)));
        Card cardB = createCardWithName("카드B", null, null, 5000, CardType.CREDIT, List.of(createBenefit(category, DiscountType.RATE, 10.0, null, 1)));
        Card cardC = createCardWithName("카드C", null, null, 5000, CardType.CHECK, List.of(createBenefit(category, DiscountType.RATE, 10.0, null, 1)));
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(cardA, cardB, cardC));

        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        // 혜택 동일 -> 연회비 낮은 순 -> 체크카드 우선
        assertThat(results.get(0).card().getName()).isEqualTo("카드C");
        assertThat(results.get(1).card().getName()).isEqualTo("카드B");
        assertThat(results.get(2).card().getName()).isEqualTo("카드A");
    }

    @Test
    @DisplayName("추천 결과에 카테고리별 상세 혜택이 포함된다")
    void recommend_includesBenefitDetails() {
        // given
        Category category = createCategory(1L);
        Card card = createCard(null, null, List.of(
                createBenefit(category, DiscountType.RATE, 10.0, null, 1)
        ));
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(card));


        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).benefitDetails()).hasSize(1);
        assertThat(results.get(0).benefitDetails().get(0).categoryName()).isEqualTo("커피/카페");
        assertThat(results.get(0).benefitDetails().get(0).discountAmount()).isEqualTo(10000);
    }

    @Test
    @DisplayName("상세 혜택에 통합 한도가 적용된다")
    void recommend_benefitDetailsRespectsLimit() {
        // given
        Category category1 = createCategory(1L);
        Category category2 = createCategory(2L);

        Card card = createCard(15000, null, List.of(
                createBenefit(category1, DiscountType.RATE, 10.0, null, 1),
                createBenefit(category2, DiscountType.RATE, 10.0, null, 2)
        ));
        when(cardRepository.findByStatus(CardStatus.ACTIVE)).thenReturn(List.of(card));

        // 각 10만원씩 -> 10% = 각 1만원 -> 총 2만원 but 한도 1.5만
        List<ExpenseSummaryRequest> expenses = List.of(
                new ExpenseSummaryRequest(1L, 100000L),
                new ExpenseSummaryRequest(2L, 100000L)
        );

        // when
        var results = recommendationService.recommend(expenses, 500000L);

        // then
        assertThat(results.get(0).benefitAmount()).isEqualTo(15000);

        long totalDetails = results.get(0).benefitDetails().stream()
                .mapToLong(BenefitDetail::discountAmount)
                .sum();
        assertThat(totalDetails).isEqualTo(15000);
    }

    private Category createCategory(long id) {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(id);
        return category;
    }

    private Card createCard(Integer totalLimit, Integer minPerformance, List<Benefit> benefits) {
        Card card = mock(Card.class);
        when(card.getTotalLimit()).thenReturn(totalLimit);
        when(card.getMinPerformance()).thenReturn(minPerformance);
        when(card.getBenefits()).thenReturn(benefits);
        when(card.getAnnualFee()).thenReturn(0);
        when(card.getCardType()).thenReturn(CardType.CREDIT);
        return card;
    }

    private Benefit createBenefit(Category category, DiscountType type, Double value, Integer minPurchase, Integer priority) {
        Benefit benefit = mock(Benefit.class);
        when(benefit.getCategory()).thenReturn(category);
        when(benefit.getDiscountType()).thenReturn(type);
        when(benefit.getDiscountValue()).thenReturn(value);
        when(benefit.getMinPurchase()).thenReturn(minPurchase);
        when(benefit.getPriority()).thenReturn(priority);
        when(benefit.getMonthlyLimit()).thenReturn(null);
        return benefit;
    }

    private Card createCardWithName(String name, Integer totalLimit, Integer minPerformance, Integer annualFee, CardType cardType, List<Benefit> benefits) {
        Card card = mock(Card.class);
        when(card.getName()).thenReturn(name);
        when(card.getTotalLimit()).thenReturn(totalLimit);
        when(card.getMinPerformance()).thenReturn(minPerformance);
        when(card.getBenefits()).thenReturn(benefits);
        when(card.getAnnualFee()).thenReturn(annualFee);
        when(card.getCardType()).thenReturn(cardType);
        return card;
    }
}