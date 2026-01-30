let rawAnalysisResults = [];

document.getElementById('excelFile').addEventListener('change', function () {
    const btn = document.getElementById('analyzeBtn');
    btn.disabled = !this.files.length;
})

async function analyzeExcel() {
    const btn = document.getElementById('analyzeBtn');
    const originalText = btn.textContent;

    btn.disabled = true;
    btn.textContent = '분석 중...';

    try {
        const file = document.getElementById('excelFile').files[0];
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch('/api/recommendation/upload', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('서버 오류가 발생했습니다.');
        }

        rawAnalysisResults = await response.json();
        renderMappingModal();
    } catch (error) {
        alert('분석 중 오류가 발생했습니다.');
    } finally {
        btn.disabled = false;
        btn.textContent = originalText;
    }
}

function renderMappingModal() {
    const body = document.getElementById('mappingTableBody');
    body.innerHTML = '';

    rawAnalysisResults.forEach((item, index) => {
        const tr = document.createElement('tr');

        const tdStore = document.createElement('td');
        tdStore.textContent = item.storeName;
        tr.appendChild(tdStore);

        const tdAmount = document.createElement('td');
        tdAmount.textContent = item.amount.toLocaleString() + '원';
        tr.appendChild(tdAmount);

        const tdCategory = document.createElement('td');
        const select = document.createElement('select');
        select.className = 'form-select mapping-select';
        select.dataset.amount = item.amount;

        CATEGORIES.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.name;
            if (item.categoryId === cat.id) option.selected = true;
            select.appendChild(option);
        });

        tdCategory.appendChild(select);
        tr.appendChild(tdCategory);
        body.appendChild(tr);
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
        if (input) input.value = formatNumber(totals[catId]);
    }
    document.getElementById('expectedPerformance').value = formatNumber(totalPerformance);

    alert('데이터가 합산되어 반영되었습니다');
    closeModal();
}

function closeModal() {
    document.getElementById('mappingModal').style.display = 'none';
}

function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

function unformatNumber(str) {
    return str.replace(/,/g, '');
}

document.querySelectorAll('.category-input, #expectedPerformance').forEach(input => {
    input.addEventListener('focus', function() {
        this.select();
    });
});

document.querySelectorAll('.category-input, #expectedPerformance').forEach(input => {
    input.addEventListener('input', function() {
        const value = this.value.replace(/[^\d]/g, '');
        if (value !== '') {
            this.value = formatNumber(value);
        } else {
            this.value = '';
        }
    });
});

document.getElementById('mainForm').addEventListener('submit', function() {
    this.querySelectorAll('.category-input, #expectedPerformance').forEach(input => {
        input.value = unformatNumber(input.value);
    });
});

