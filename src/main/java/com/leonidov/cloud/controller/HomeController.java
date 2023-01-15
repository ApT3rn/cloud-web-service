package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String homePage(@ModelAttribute(name = "user") User user) {
        return "home";
    }
}

