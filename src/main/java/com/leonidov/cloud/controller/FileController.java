package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

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
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Path path = Paths.get(
                    fileService.getUserFolder(user.getEmail()) + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
        attributes.addFlashAttribute("message", "Файл успешно загружен!");
        return "redirect:/user";
    }

    @PostMapping("/createFolder")
    public void createFolder(String name) {
        User user = Mediator.getUser();
        fileService.createFolderForUser(user.getEmail(), name);
    }

    @DeleteMapping("/delete/{file}")
    public String deleteFile(@PathVariable("file") MultipartFile file, RedirectAttributes attributes) {
        User user = Mediator.getUser();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Path path = Paths.get(
                    fileService.getUserFolder(user.getEmail()) + fileName);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/user";
    }

    @GetMapping("/file/get/{file}")
    public File getFile(@PathVariable("file") String file) {
        User user = Mediator.getUser();
        return fileService.getFile(user.getEmail(), file);
    }
}
