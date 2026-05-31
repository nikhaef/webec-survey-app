package com.example.surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.surveyapp.model.*;
import com.example.surveyapp.repository.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class RespondentController {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final ResponseRepository responseRepository;

    public RespondentController(SurveyRepository surveyRepository, UserRepository userRepository, ResponseRepository responseRepository) {
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
        this.responseRepository = responseRepository;
    }

    @GetMapping("/survey/{id}/take")
    public String take(@PathVariable Long id, Model model, HttpSession session) {
        var sOpt = surveyRepository.findById(id);
        if (sOpt.isEmpty() || !sOpt.get().isOpen()) return "redirect:/";
        model.addAttribute("survey", sOpt.get());
        return "take_survey";
    }

    @PostMapping("/survey/{id}/submit")
    public String submit(@PathVariable Long id, HttpServletRequest req, HttpSession session, Model model) {
        var sOpt = surveyRepository.findById(id);
        if (sOpt.isEmpty() || !sOpt.get().isOpen()) return "redirect:/";
        Survey s = sOpt.get();
        Object uid = session.getAttribute("userId");
        if (!(uid instanceof Long)) {
            model.addAttribute("error", "Sie müssen angemeldet sein, um die Umfrage auszufüllen.");
            model.addAttribute("survey", s);
            return "take_survey";
        }
        var uOpt = userRepository.findById((Long) uid);
        if (uOpt.isEmpty()) return "redirect:/";
        User user = uOpt.get();
        if (responseRepository.findBySurveyAndRespondent(s, user).isPresent()) {
            model.addAttribute("error", "Sie haben diese Umfrage bereits ausgefüllt.");
            model.addAttribute("survey", s);
            return "take_survey";
        }

        Response resp = new Response();
        resp.setSurvey(s);
        resp.setRespondent(user);

        // For each question expect parameter q{questionId}
        for (Question q : s.getQuestions()) {
            String param = req.getParameter("q" + q.getId());
            if (param == null || param.isEmpty()) {
                model.addAttribute("error", "Bitte alle Fragen beantworten.");
                model.addAttribute("survey", s);
                return "take_survey";
            }
            try {
                Long optId = Long.parseLong(param);
                Option selected = null;
                for (Option o : q.getOptions()) if (o.getId().equals(optId)) selected = o;
                if (selected == null) {
                    model.addAttribute("error", "Ungültige Auswahl.");
                    model.addAttribute("survey", s);
                    return "take_survey";
                }
                Answer a = new Answer();
                a.setQuestion(q);
                a.setSelected(selected);
                a.setResponse(resp);
                resp.getAnswers().add(a);
            } catch (NumberFormatException ex) {
                model.addAttribute("error", "Ungültige Auswahl.");
                model.addAttribute("survey", s);
                return "take_survey";
            }
        }

        responseRepository.save(resp);
        return "redirect:/survey/" + id + "/results";
    }
}

