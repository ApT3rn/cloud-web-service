package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final FileService fileService;

    @Autowired
    public UserController(FileService fileService) {
        this.fileService = fileService;
    }

    private User getUser() {
        return Mediator.getUser();
    }

    @GetMapping
    public String userPage(Model model) {
        List<File> allFiles = fileService.allFiles(getUser().getEmail(), "");
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        return "user";
    }

    @GetMapping("/file=({file})")
    public String inDirectory(@PathVariable("file") String file, Model model) {
        List<File> allFiles = fileService.allFiles(getUser().getEmail(), file);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        return "user-in-folder";
    }
}
