package org.example.cardfit.recommendation.controller;

import org.example.cardfit.recommendation.form.ManualInputForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/recommendation")
public class RecommendationViewController {

    @GetMapping("/manual")
    public String manualInputPage(Model model) {
        model.addAttribute("manualInputForm", new ManualInputForm(new ArrayList<>(), 0L));
        return "recommendation/input";
    }
}
