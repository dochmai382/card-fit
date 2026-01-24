package org.example.cardfit.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.example.cardfit.recommendation.dto.RecommendationResponse;
import org.example.cardfit.recommendation.form.ManualInputForm;
import org.example.cardfit.recommendation.mapper.RecommendationMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationApiController {

    private final RecommendationMapper recommendationMapper;

    @PostMapping("/manual")
    public ResponseEntity<RecommendationResponse> getManualRecommendation(@RequestBody ManualInputForm form) {
        var requests = recommendationMapper.toSummaryRequest(form);
        System.out.println("가공된 데이터 개수: " + requests.size());

        return ResponseEntity.ok(new RecommendationResponse(
                "테스트카드", 0, List.of(), "엔진 구현 중", 0
        ));
    }
}
