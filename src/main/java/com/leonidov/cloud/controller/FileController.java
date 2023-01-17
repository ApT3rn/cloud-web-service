package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        User user = Mediator.getUser();
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Пожалуйста выберите файл для загрузки");
            return "redirect:/user";
        }
        fileService.uploadFile(user.getEmail(), file);
        attributes.addFlashAttribute("message", "Файл успешно загружен!");
        return "redirect:/user";
    }


    @PostMapping("/createFolder")
    public void createFolder(String name) {
        User user = Mediator.getUser();
        fileService.createFolderForUser(user.getEmail(), name);
    }

    @PostMapping("/delete/{file}")
    public String deleteFile(@PathVariable("file") String file, RedirectAttributes attributes) {
        User user = Mediator.getUser();
        fileService.deleteFile(user.getEmail(), file);
        return "redirect:/user";
    }

    @GetMapping("/file/get/{file}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("file") String file) {
        User user = Mediator.getUser();
        return fileService.getFile(user.getEmail(), file);
    }
}
