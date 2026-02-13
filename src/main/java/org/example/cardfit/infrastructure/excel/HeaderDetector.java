package org.example.cardfit.infrastructure.excel;

import lombok.RequiredArgsConstructor;
import org.example.cardfit.global.error.BusinessException;
import org.example.cardfit.global.error.ErrorCode;
import org.example.cardfit.infrastructure.llm.LLMClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HeaderDetector {
    private final LLMClient llmClient;

    private static final List<String> DATE_KEYWORDS = List.of("날짜", "일자", "이용일", "사용일", "거래일", "date");
    private static final List<String> STORE_KEYWORDS = List.of("가맹점", "상호", "거래처", "내역", "사용처", "store");
    private static final List<String> AMOUNT_KEYWORDS = List.of("금액", "결제", "승인", "이용금액", "amount");
    private static final List<String> TYPE_KEYWORDS = List.of("수입", "지출", "구분", "유형", "입출금", "type");

    public ColumnMapping detect(List<String> headers) {
        int dateIdx = findByKeywords(headers, DATE_KEYWORDS);
        int storeIdx = findByKeywords(headers, STORE_KEYWORDS);
        int amountIdx = findByKeywords(headers, AMOUNT_KEYWORDS);
        int typeIdx = findByKeywords(headers, TYPE_KEYWORDS);

        ColumnMapping mapping;
        if (dateIdx >= 0 && storeIdx >= 0 && amountIdx >= 0) {
            mapping = new ColumnMapping(dateIdx, storeIdx, amountIdx, typeIdx);
        } else {
            mapping = detectByLLM(headers);
        }

        validateMapping(mapping);
        return mapping;
    }

    private void validateMapping(ColumnMapping mapping) {
        if (mapping.dateIndex() < 0 || mapping.storeNameIndex() < 0 || mapping.amountIndex() < 0) {
            throw new BusinessException(ErrorCode.EXCEL_REQUIRED_COLUMNS_MISSING);
        }
    }

    private int findByKeywords(List<String> headers, List<String> keywords) {
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i).toLowerCase();
            for (String keyword: keywords) {
                if (header.contains(keyword.toLowerCase())) return i;
            }
        }
        return -1;
    }

    private ColumnMapping detectByLLM(List<String> headers) {
        try {
            return llmClient.detectColumns(headers);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXCEL_UNSUPPORTED_FORMAT);
        }
    }
}
