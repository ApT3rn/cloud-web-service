package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.CheckAuthenticated;
import com.leonidov.cloud.config.security.MyUserDetails;
import com.leonidov.cloud.model.enums.UserStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PricingController {

    private CheckAuthenticated checkAuthenticated = new CheckAuthenticated();

    @GetMapping("/pricing")
    public String pricingPage(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        if (checkAuthenticated.isAuthenticated())
            model.addAttribute("userstatus", userDetails.getUser().getStatus().toString());
        else
            model.addAttribute("userstatus", UserStatus.DEFAULT.toString());
        return "pricing";
    }

    @GetMapping("/pricing/{status}")
    public String buyRate(@PathVariable("status") String status, Model model) {
        if (!checkAuthenticated.isAuthenticated())
            return "redirect:/login";

        if (status.equals("lite")) {
            model.addAttribute("pricing", UserStatus.LITE.toString());
            model.addAttribute("price", "Оплатить 149₽");
        } else if (status.equals("standart")) {
            model.addAttribute("pricing", UserStatus.STANDART.toString());
            model.addAttribute("price", "Оплатить 249₽");
        } else {
            model.addAttribute("pricing", UserStatus.PLUS.toString());
            model.addAttribute("price", "Оплатить 349₽");
        }
        return "pricing-buy";
    }
}
