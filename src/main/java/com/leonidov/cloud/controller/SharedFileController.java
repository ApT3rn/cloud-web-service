package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.SharedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/file")
public class SharedFileController {

    private final SharedFileService sharedFileService;

    @Autowired
    private SharedFileController(SharedFileService sharedFileService) {
        this.sharedFileService = sharedFileService;
    }

    private User getUser() {
        return Mediator.getUser();
    }

    private String getUserId() {
        return getUser().getId().toString();
    }

    @GetMapping({"id"})
    public String getFile(@PathVariable("id") String id, Model model) {
        File file = sharedFileService.getSharedFile(id);
        if (file != null) {
            model.addAttribute("file", file);
            return "file";
        }
        return "404";
    }



}
