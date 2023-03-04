package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.CheckAuthenticated;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    private SignupController(UserService userService) {
        this.userService = userService;
        this.checkAuthenticated = new CheckAuthenticated();
    }

    @GetMapping
    public String signupPage(@ModelAttribute(name = "user") User user) {
        if (checkAuthenticated.isAuthenticated())
            return "redirect:/user";
        return "signup";
    }

    @PostMapping
    public String signupUser(@Valid @ModelAttribute(name = "user") User user) {
        boolean success = userService.save(user);
        if (success)
            return "redirect:/login";
        System.out.println("Данная электронная почта уже зарегистрирована!");
        return "signup";

    }
}
