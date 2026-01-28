let rawAnalysisResults = [];

async function analyzeExcel() {
    const file = document.getElementById('excelFile').files[0];
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('/api/recommendation/upload', {
        method: 'POST',
        body: formData
    });
    rawAnalysisResults = await response.json();

    renderMappingModal();
}

function renderMappingModal() {
    const body = document.getElementById('mappingTableBody');
    body.innerHTML = '';

    rawAnalysisResults.forEach((item, index) => {
        const categoryOptions = CATEGORIES.map(cat => {
            const selected = item.categoryType === cat.toString() ? 'selected'
                : '';
            return `<option value="${cat.id}" ${selected}>${cat.name}</option>`;
        }).join('');
        body.innerHTML += `
            <tr>
                  <td>${item.storeName}</td>
                  <td>${item.amount.toLocaleString()}원</td>
                  <td>
                      <select class="form-select mapping-select"  data-amount="${item.amount}">
                          ${categoryOptions}
                      </select>
                  </td>
              </tr>`;
    });
    document.getElementById('mappingModal').style.display = 'block';
}

function applyToManualForm() {
    const selects = document.querySelectorAll('.mapping-select');
    const totals = {};
    let totalPerformance = 0;

    selects.forEach(select => {
        const catId = select.value;
        const amount = parseInt(select.dataset.amount);

        totals[catId] = (totals[catId] || 0) + amount;
        totalPerformance += amount;
    });

    for (const catId in totals) {
        const input = document.getElementById('amount-' + catId);
        if (input) input.value = totals[catId];
    }
    document.getElementById('expectedPerformance').value = totalPerformance;

    alert('데이터가 합산되어 반영되었습니다');
    closeModal();
}

function closeModal() {
    document.getElementById('mappingModal').style.display = 'none';
}