package org.example.cardfit.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cardfit.global.error.BusinessException;
import org.example.cardfit.global.error.ErrorCode;
import org.example.cardfit.infrastructure.excel.PoiExcelParser;
import org.example.cardfit.infrastructure.llm.LLMClient;
import org.example.cardfit.recommendation.dto.ExpenseMappingResult;
import org.example.cardfit.recommendation.dto.RawExpense;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseParseService {

    private final PoiExcelParser excelParser;
    private final LLMClient llmClient;
    private final ObjectMapper objectMapper;

    public List<ExpenseMappingResult> parseAndClassify(MultipartFile file) {
        log.info("엑셀 파싱 요청: 파일명 {}", file.getOriginalFilename());

        validateFile(file);

        var rawExpenses = excelParser.parse(file);

        List<String> distinctStores = rawExpenses.stream()
                .map(RawExpense::storeName)
                .distinct()
                .toList();

        Map<String, Long> categoryMap = classifyStores(distinctStores);

        List<ExpenseMappingResult> result = rawExpenses.stream()
                .map(raw -> new ExpenseMappingResult(
                        raw.storeName(),
                        Long.valueOf(raw.amount().toString()),
                        raw.date().toString(),
                        categoryMap.getOrDefault(raw.storeName(), 0L)
                ))
                .toList();

        log.info("엑셀 파싱 완료: {}건 처리", result.size());
        return result;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException(ErrorCode.FILE_EMPTY);

        String filename = file.getOriginalFilename();
        if (filename == null) throw new BusinessException(ErrorCode.FILE_NAME_MISSING);

        String lower = filename.toLowerCase();
        if (!lower.endsWith(".xlsx") && !lower.endsWith(".xls"))
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
    }

    private Map<String, Long> classifyStores(List<String> stores) {
        String jsonResult = llmClient.classify(stores);

        try {
            List<Map<String, Object>> list = objectMapper.readValue(jsonResult, new TypeReference<>() {
            });
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("storeName"),
                    m -> Long.valueOf(m.get("categoryId").toString()),
                    (existing, replacement) -> existing
            ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
