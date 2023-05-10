package com.leonidov.cloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PricingController {

    @GetMapping("/pricing")
    public String pricingPage() {
        return "pricing";
    }
}
