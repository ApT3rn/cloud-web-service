package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.MyUserDetails;
import com.leonidov.cloud.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Time;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String homePage(@ModelAttribute(name = "user") User user, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        return "home";
    }
}

