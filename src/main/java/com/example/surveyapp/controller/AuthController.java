package com.example.surveyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.surveyapp.model.User;
import com.example.surveyapp.model.UserRole;
import com.example.surveyapp.repository.UserRepository;

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
    public String doRegister(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Benutzername bereits vergeben");
            return "register";
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(UserRole.RESPONDENT);
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

