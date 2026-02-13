package org.example.cardfit.global.error;

import org.example.cardfit.recommendation.controller.RecommendationApiController;
import org.example.cardfit.recommendation.form.ManualInputForm;
import org.example.cardfit.recommendation.service.ExpenseParseService;
import org.example.cardfit.recommendation.service.LlmExplanationService;
import org.example.cardfit.recommendation.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseParseService expenseParseService;

    @MockitoBean
    private RecommendationService recommendationService;

    @MockitoBean
    private LlmExplanationService llmExplanationService;

    @Test
    @DisplayName("음수 금액 입력시 validation 에러 반환")
    void negativeAmount_returnsValidationError() throws Exception {
        // given
        ManualInputForm form = new ManualInputForm(
                List.of(new ManualInputForm.ManualItem(1L, -1000L)),
                100000L
        );

        // when & then
        mockMvc.perform(post("/api/recommendation/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다"))
                .andExpect(jsonPath("$.details").isArray());

    }

    @Test
    @DisplayName("음수 예상 실적 입력 시 validation 에러 반환")
    void negativePerformance_returnsValidationError() throws Exception {
        // given
        ManualInputForm form = new ManualInputForm(
                List.of(new ManualInputForm.ManualItem(1L, 10000L)),
                -100000L
        );

        // when & then
        mockMvc.perform(post("/api/recommendation/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다"));
    }

}