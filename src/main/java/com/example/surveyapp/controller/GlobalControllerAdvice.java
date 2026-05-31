package com.example.surveyapp.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.surveyapp.model.User;
import com.example.surveyapp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    public GlobalControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id instanceof Long) {
            return userRepository.findById((Long) id).orElse(null);
        }
        return null;
    }
}

