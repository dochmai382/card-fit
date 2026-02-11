package org.example.cardfit.recommendation.controller;

import org.example.cardfit.domain.category.CategoryType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recommendation")
public class RecommendationViewController {

    @GetMapping("/input")
    public String inputPage(Model model) {
        model.addAttribute("categories", CategoryType.values());

        List<Map<String, Object>> categoriesForJs = Arrays.stream(CategoryType.values())
                .map(cat -> Map.<String, Object>of("id", cat.getId(), "name", cat.getName()))
                .toList();
        model.addAttribute("categoriesForJs", categoriesForJs);

        return "recommendation/input";
    }

    @GetMapping("/result")
    public String showResult() {
        return "recommendation/result";
    }
}
