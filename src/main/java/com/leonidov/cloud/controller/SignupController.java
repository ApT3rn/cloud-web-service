package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.CheckAuthenticated;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private final UserService userService;
    private final CheckAuthenticated checkAuthenticated;

    @Autowired
    public SignupController(UserService userService) {
        this.userService = userService;
        this.checkAuthenticated = new CheckAuthenticated();
    }

    @GetMapping
    public String signupPage(@ModelAttribute(name = "user") User user) {
        return checkAuthenticated.isAuthenticated() ? "redirect:/user" : "signup";
    }

    @PostMapping
    public String signupUser(@Valid @ModelAttribute(name = "user") User user, Model model) {
        if (userService.save(user))
            return "redirect:/login";
        model.addAttribute("message", "Данная электронная почта уже зарегистрирована!");
        return "signup";

    }
}
