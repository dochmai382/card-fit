package org.example.cardfit.recommendation.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ExpenseParseService {

    private final PoiExcelParser excelParser;
    private final LLMClient llmClient;
    private final ObjectMapper objectMapper;

    public List<ExpenseMappingResult> parseAndClassify(MultipartFile file) {
        validateFile(file);

        var rawExpenses = excelParser.parse(file);

        List<String> distinctStores = rawExpenses.stream()
                .map(RawExpense::storeName)
                .distinct()
                .toList();

        Map<String, Long> categoryMap = classifyStores(distinctStores);

        return rawExpenses.stream()
                .map(raw -> new ExpenseMappingResult(
                        raw.storeName(),
                        Long.valueOf(raw.amount().toString()),
                        raw.date().toString(),
                        categoryMap.getOrDefault(raw.storeName(), 0L)
                ))
                .toList();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("파일이 비어있습니다");

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")))
            throw new IllegalArgumentException("엑셀 파일(.xlsx, .xls)만 업로드 가능합니다.");
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
