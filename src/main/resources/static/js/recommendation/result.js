const DEFAULT_CARD_IMAGE = 'data:image/svg+xml,' +
    encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" width="120" height="76" viewBox="0 0 120 76">
        <rect fill="#e0e0e0" width="120" height="76" rx="8"/>
        <text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="#999" font-size="12">No Image</text>
    </svg>
    `);


document.addEventListener('DOMContentLoaded', function() {
    const resultContainer = document.getElementById('result');
    const data = sessionStorage.getItem('recommendations');

    if (!data) {
        showError('추천 결과가 없습니다. 다시 시도해주세요');
        return;
    }

    let recommendations;
    try {
        recommendations = JSON.parse(data);
    } catch (e) {
        showError('데이터를 불러오는 중 오류가 발생했습니다.');
        return;
    }
    sessionStorage.removeItem('recommendations');

    if (recommendations.length === 0) {
        showError('조건에 맞는 카드가 없습니다.');
        return;
    }

    resultContainer.innerHTML = '';
    recommendations.forEach((rec, index) => {
        resultContainer.appendChild(createCardElement(rec, index));
    })

    function showError(message) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error';
        errorDiv.textContent = message;
        resultContainer.innerHTML = '';
        resultContainer.appendChild(errorDiv);
    }

    function createCardElement(rec, index) {
        const cardItem = document.createElement('div');
        cardItem.className = 'card p-3 mb-3 card-item';

        const rank = document.createElement('div');
        rank.className = 'rank';
        rank.textContent = index + 1;

        const img = document.createElement('img');
        img.className = 'card-image';
        img.alt = rec.cardName;
        img.onerror = function() {
            this.src = DEFAULT_CARD_IMAGE;
            this.onerror = null;
        };
        img.src = rec.cardImageUrl || DEFAULT_CARD_IMAGE;

        const cardInfo = document.createElement('div');
        cardInfo.className = 'card-info';

        const cardName = document.createElement('div');
        cardName.className = 'fw-bold fs-5';
        cardName.textContent = rec.cardName;

        const issuer = document.createElement('div');
        issuer.className = 'text-muted small';
        issuer.textContent = rec.issuer;

        const benefit = document.createElement('div');
        benefit.className = 'benefit-amount my-2';
        benefit.textContent = `월 약 ${(rec.expectedBenefitAmount || 0).toLocaleString()}원 혜택`;

        const annualFee = document.createElement('div');
        annualFee.className = 'text-muted small';
        annualFee.textContent = `연회비 ${(rec.annualFee || 0).toLocaleString()}원`;

        const explanation = document.createElement('div');
        explanation.className = 'explanation p-2 mt-2 small';
        explanation.textContent = rec.explanation;

        cardInfo.append(cardName, issuer, benefit, annualFee,
            explanation);
        cardItem.append(rank, img, cardInfo);

        return cardItem;
    }
})