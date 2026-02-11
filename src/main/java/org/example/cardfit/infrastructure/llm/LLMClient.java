package org.example.cardfit.infrastructure.llm;

import org.example.cardfit.infrastructure.excel.ColumnMapping;

import java.util.List;

public interface LLMClient {
    String classify(List<String> storeNames);

    ColumnMapping detectColumns(List<String> headers);

    String generateExplanation(String prompt);
}
