package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final FileService fileService;
    private final UserService userService;

    @Autowired
    private UserController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    private User getUser() {
        return Mediator.getUser();
    }

    private String getUserId() {
        return getUser().getId().toString();
    }

    @GetMapping
    public String userPage(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListFiles(getUserId(), "*"));
        return "user";
    }

    @GetMapping("{path}")
    public String userInDirectory(@PathVariable("path") String path, Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListFiles(getUserId(), path));
        model.addAttribute("url", "+");
        return "user";
    }

    @PostMapping("search")
    public String searchFiles(@RequestParam("filename") String filename, Model model) {
        if (filename.isEmpty())
            return "redirect:/user";
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.searchFiles(getUserId(), filename));
        model.addAttribute("url", "+");
        return "user";
    }

    @GetMapping("settings")
    public String settingsPage(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("url", "+");
        return "settings";
    }

    @PostMapping("settings")
    public String settingsPage(User user) {
        userService.saveAndFlush(user);
        return "redirect:/user/settings";
    }
}
