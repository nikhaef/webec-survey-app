package com.example.surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.surveyapp.repository.SurveyRepository;

@Controller
public class HomeController {

    private final SurveyRepository surveyRepository;

    public HomeController(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("openSurveys", surveyRepository.findByOpenTrue());
        model.addAttribute("closedSurveys", surveyRepository.findByOpenFalse());
        return "index";
    }
}

