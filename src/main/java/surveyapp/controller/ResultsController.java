package surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import surveyapp.model.*;
import surveyapp.repository.ResponseRepository;
import surveyapp.repository.SurveyRepository;

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

    // Map ratio in [0,1] to a color between red(255,0,0) -> yellow(255,255,0) -> green(0,128,0)
    private String colorForRatio(double r) {
        if (r < 0) r = 0; if (r > 1) r = 1;
        int red, green, blue;
        if (r <= 0.5) {
            double t = r / 0.5;
            red = 255;
            green = (int)Math.round(0 + t * 255);
            blue = 0;
        } else {
            double t = (r - 0.5) / 0.5;
            red = (int)Math.round(255 + t * (0 - 255));
            green = (int)Math.round(255 + t * (128 - 255));
            blue = 0;
        }
        return toRgba(red, green, blue, 0.15);
    }

    private String toRgba(int r, int g, int b, double a) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        a = Math.max(0, Math.min(1, a));
        return String.format("rgba(%d,%d,%d,%.2f)", r, g, b, a);
    }

    @GetMapping("/survey/{id}/results")
    @Transactional(readOnly = true)
    public String results(@PathVariable Long id, Model model) {
        try {
            var sOpt = surveyRepository.findById(id);
            if (sOpt.isEmpty()) return "redirect:/";
            Survey s = sOpt.get();
            List<Response> responses = responseRepository.findBySurvey(s);

            // Map questionId -> optionId -> count
            Map<Long, Map<Long, Integer>> counts = new HashMap<>();
            for (Question q : s.getQuestions()) {
                Map<Long, Integer> m = new HashMap<>();
                for (Option o : q.getOptions()) {
                    m.put(o.getId(), 0);
                }
                counts.put(q.getId(), m);
            }
            for (Response r : responses) {
                for (Answer a : r.getAnswers()) {
                    if (a.getQuestion() != null && a.getSelected() != null) {
                        Long questionId = a.getQuestion().getId();
                        Long optionId = a.getSelected().getId();
                        Map<Long, Integer> m = counts.get(questionId);
                        if (m != null) {
                            m.put(optionId, m.getOrDefault(optionId, 0) + 1);
                        }
                    }
                }
            }

            // Compute colors per option based on counts (red -> yellow -> green)
            Map<Long, Map<Long, String>> bgMap = new HashMap<>();
            for (Question q : s.getQuestions()) {
                Map<Long, Integer> m = counts.get(q.getId());
                if (m == null) continue;
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                for (Integer v : m.values()) {
                    if (v < min) min = v;
                    if (v > max) max = v;
                }
                if (min == Integer.MAX_VALUE) { min = 0; max = 0; }
                Map<Long, String> optColors = new HashMap<>();
                for (Option o : q.getOptions()) {
                    int v = m.getOrDefault(o.getId(), 0);
                    String color;
                    // If this option has the maximum count, mark it green (also covers ties)
                    if (v == max && max > 0) {
                        color = toRgba(0,128,0,0.14);
                    } else if (v == min && max != min) {
                        // least chosen -> light red
                        color = toRgba(255,120,120,0.12);
                    } else if (max == min) {
                        // all equal (including zero) -> neutral subtle background
                        color = toRgba(200,200,200,0.06);
                    } else {
                        double ratio = (double)(v - min) / (double)(max - min);
                        color = colorForRatio(ratio);
                    }
                    optColors.put(o.getId(), color);
                }
                bgMap.put(q.getId(), optColors);
            }

            model.addAttribute("survey", s);
            model.addAttribute("counts", counts);
            model.addAttribute("bgMap", bgMap);
            model.addAttribute("responsesCount", responses.size());
            return "results";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Fehler beim Laden der Ergebnisse: " + e.getMessage());
            return "redirect:/";
        }
    }
}

