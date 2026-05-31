package surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import surveyapp.model.Option;
import surveyapp.model.Question;
import surveyapp.model.Survey;
import surveyapp.repository.SurveyRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CoordinatorController {

    private final SurveyRepository surveyRepository;

    public CoordinatorController(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @GetMapping("/coordinator/results")
    public String resultsList(Model model) {
        // show closed surveys (results available)
        model.addAttribute("closedSurveys", surveyRepository.findByOpenFalse());
        return "coordinator_results";
    }

    @GetMapping("/coordinator/create")
    public String createForm() {
        return "create_survey";
    }

    @PostMapping("/coordinator/save")
    public String saveSurvey(HttpServletRequest req, Model model) {
        String title = req.getParameter("title");
        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("error", "Titel ist erforderlich");
            return "create_survey";
        }
        Survey s = new Survey();
        s.setTitle(title.trim());

        // parse up to 10 questions and up to 5 options each
        for (int qi = 0; qi < 10; qi++) {
            String qKey = "q" + qi + "text";
            String qText = req.getParameter(qKey);
            if (qText == null || qText.trim().isEmpty()) continue;
            Question q = new Question();
            q.setText(qText.trim());
            q.setSurvey(s);
            for (int oi = 0; oi < 5; oi++) {
                String oKey = "q" + qi + "opt" + oi;
                String oText = req.getParameter(oKey);
                if (oText == null || oText.trim().isEmpty()) continue;
                Option o = new Option();
                o.setText(oText.trim());
                o.setQuestion(q);
                q.getOptions().add(o);
            }
            if (q.getOptions().size() >= 1) {
                s.getQuestions().add(q);
            }
        }
        if (s.getQuestions().size() < 1 || s.getQuestions().size() > 10) {
            model.addAttribute("error", "Erforderlich: 1-10 Fragen mit jeweils 1-5 Optionen");
            return "create_survey";
        }
        surveyRepository.save(s);
        return "redirect:/";
    }

    @GetMapping("/coordinator/open/{id}")
    public String openSurvey(@PathVariable Long id) {
        surveyRepository.findById(id).ifPresent(s -> { s.setOpen(true); surveyRepository.save(s); });
        return "redirect:/";
    }

    @GetMapping("/coordinator/close/{id}")
    public String closeSurvey(@PathVariable Long id) {
        surveyRepository.findById(id).ifPresent(s -> { s.setOpen(false); surveyRepository.save(s); });
        return "redirect:/";
    }
}

