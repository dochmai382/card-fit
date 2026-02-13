package org.example.cardfit.domain.card;

import org.example.cardfit.domain.benefit.Benefit;
import org.example.cardfit.domain.benefit.DiscountType;
import org.example.cardfit.domain.category.Category;
import org.example.cardfit.domain.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카드를 저장할 때 혜택 리스트가 함께 저장되고 조회되어야 한다")
    void card_benefits_load_test() {
        Category category = new Category("테스트_식비_" + System.currentTimeMillis(), "스타벅스,식당");
        categoryRepository.save(category);

        Card card = Card.builder()
                .issuer("신한")
                .name("Mr.Life")
                .cardType(CardType.CREDIT)
                .build();

        Benefit benefit = Benefit.builder()
                .card(card)
                .category(category)
                .discountType(DiscountType.RATE)
                .discountValue(10.0)
                .build();

        card.getBenefits().add(benefit);

        Card savedCard = cardRepository.save(card);
        cardRepository.flush();

        Card findCard = cardRepository.findById(savedCard.getId()).orElseThrow();

        assert (!findCard.getBenefits().isEmpty());
        assert (findCard.getBenefits().get(0).getDiscountValue() == 10.0);
    }
}