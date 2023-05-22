package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.CheckAuthenticated;
import com.leonidov.cloud.model.enums.UserStatus;
import com.leonidov.cloud.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class PricingController {

    private CheckAuthenticated checkAuthenticated;
    private UserService userService;

    public PricingController(UserService userService) {
        this.userService = userService;
        checkAuthenticated = new CheckAuthenticated();
    }

    @GetMapping("/pricing")
    public String pricingPage(Model model, Principal principal) {
        if (checkAuthenticated.isAuthenticated())
            model.addAttribute("userstatus", userService.findUserByEmail(principal.getName()).get().getStatus().toString());
        else
            model.addAttribute("userstatus", UserStatus.DEFAULT.toString());
        return "pricing";
    }

    @PostMapping("/pricing")
    public String buyRate() {
        return null;
    }
}
