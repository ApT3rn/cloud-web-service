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
    private CheckAuthenticated check =
            new CheckAuthenticated();

    @Autowired
    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String signupPage(@ModelAttribute(name = "user") User user) {
        if (check.isAuthenticated()) {
            return "redirect:/user";
        }
        return "signup";
    }

    /*
    Метод регистрации пользователя, он проверяет базу данных на наличия пользователя
    по данному email, если пользователя с таким email не существует, он его зарегистрирует,
    а если существует пользователь с таким email, он отдаст ответ пользователю.
     */
    @PostMapping
    public String signupUser(@Valid @ModelAttribute(name = "user") User user) {
        boolean success = userService.save(user);
        if (success) {
            return "redirect:/user";
        } else {
            System.out.println("Данная электронная почта уже зарегистрирована!");
            return "signup";
        }
    }
}
