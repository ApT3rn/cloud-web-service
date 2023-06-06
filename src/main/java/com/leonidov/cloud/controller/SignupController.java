package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.CheckAuthenticated;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.model.enums.Role;
import com.leonidov.cloud.model.enums.UserStatus;
import com.leonidov.cloud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/signup")
@AllArgsConstructor
public class SignupController {

    private final UserService userService;
    private final CheckAuthenticated checkAuthenticated =
            new CheckAuthenticated();

    @GetMapping
    public String signupPage(@ModelAttribute(name = "user") User user) {
        return checkAuthenticated.isAuthenticated() ? "redirect:/user" : "signup";
    }

    @PostMapping
    public String signupUser(@RequestParam("name") String name,
                             @RequestParam("surname") String surname,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             Model model) {
        User userCreate = new User(name, surname, email, password);
        if (userService.save(userCreate))
            return "redirect:/login";
        model.addAttribute("message", "Данная электронная почта уже зарегистрирована!");
        return "signup";

    }
}
