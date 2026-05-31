package com.example.surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.surveyapp.model.*;
import com.example.surveyapp.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResultsController {

    private final SurveyRepository surveyRepository;
    private final ResponseRepository responseRepository;

    public ResultsController(SurveyRepository surveyRepository, ResponseRepository responseRepository) {
        this.surveyRepository = surveyRepository;
        this.responseRepository = responseRepository;
    }

    @GetMapping("/survey/{id}/results")
    public String results(@PathVariable Long id, Model model) {
        var sOpt = surveyRepository.findById(id);
        if (sOpt.isEmpty()) return "redirect:/";
        Survey s = sOpt.get();
        List<Response> responses = responseRepository.findBySurvey(s);

        // Map questionId -> optionId -> count
        Map<Long, Map<Long, Integer>> counts = new HashMap<>();
        for (Question q : s.getQuestions()) {
            Map<Long, Integer> m = new HashMap<>();
            for (Option o : q.getOptions()) m.put(o.getId(), 0);
            counts.put(q.getId(), m);
        }
        for (Response r : responses) {
            for (Answer a : r.getAnswers()) {
                var m = counts.get(a.getQuestion().getId());
                if (m != null) m.put(a.getSelected().getId(), m.getOrDefault(a.getSelected().getId(), 0) + 1);
            }
        }

        model.addAttribute("survey", s);
        model.addAttribute("counts", counts);
        model.addAttribute("responsesCount", responses.size());
        return "results";
    }
}

