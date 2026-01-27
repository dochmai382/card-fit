package org.example.cardfit.infrastructure.llm;

import java.util.List;

public interface LLMClient {
    String classify(List<String> storeNames);
}
