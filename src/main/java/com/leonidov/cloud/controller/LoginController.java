package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.CheckAuthenticated;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final CheckAuthenticated checkAuthenticated;
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
        this.checkAuthenticated = new CheckAuthenticated();
    }

    @GetMapping("/login")
    public String loginPage() {
        return checkAuthenticated.isAuthenticated() ? "redirect:/user" : "login";
    }

    @PostMapping("/forget")
    public String forgetPassword(@RequestParam String email, Model model) {
        if (userService.findUserByEmail(email).isPresent())
            model.addAttribute("message", "Новый пароль отправлен на вашу электронную почту!");
        else
            model.addAttribute("message", "Данный электронная почта не зарегистрирована!");
        return "login";
    }
}
