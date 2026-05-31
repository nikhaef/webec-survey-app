package surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import surveyapp.model.User;
import surveyapp.model.UserRole;
import surveyapp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        var opt = userRepository.findByUsername(username);
        if (opt.isPresent() && opt.get().getPassword().equals(password)) {
            session.setAttribute("userId", opt.get().getId());
            return "redirect:/";
        }
        model.addAttribute("error", "Ungültiger Benutzername oder Passwort");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String password,
                             @RequestParam(required = false) String coordinator,
                             HttpSession session, Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Benutzername bereits vergeben");
            return "register";
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        // If the checkbox 'coordinator' was checked, register as COORDINATOR, otherwise RESPONDENT
        if (coordinator != null && coordinator.equals("true")) {
            u.setRole(UserRole.COORDINATOR);
        } else {
            u.setRole(UserRole.RESPONDENT);
        }
        userRepository.save(u);
        session.setAttribute("userId", u.getId());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

