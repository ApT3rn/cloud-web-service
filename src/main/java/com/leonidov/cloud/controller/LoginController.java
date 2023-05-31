package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.CheckAuthenticated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final CheckAuthenticated checkAuthenticated =
            new CheckAuthenticated();

    @GetMapping("/login")
    public String loginPage() {
        return checkAuthenticated.isAuthenticated() ? "redirect:/user" : "login";
    }
}
