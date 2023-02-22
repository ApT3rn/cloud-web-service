package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FileController {

    private final FileService fileService;

    @Autowired
    private FileController(FileService fileService) {
        this.fileService = fileService;
    }

    private User getUser() {
        return Mediator.getUser();
    }

    private String getUserId() {
        return getUser().getId().toString();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("path") String path,
                             Model model, RedirectAttributes attributes) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Пожалуйста выберите файл для загрузки");
            return "user";
        }
        fileService.uploadFile(getUserId(), path, file);
        List<File> allFiles = fileService.allFiles(getUserId(), path);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        attributes.addFlashAttribute("message", "Файл успешно загружен!");
        return "user";
    }


    @PostMapping("/createFolder")
    public String createFolder(@RequestParam("path") String path,
                             @RequestParam("name") String name, Model model) {
        fileService.createFolderForUser(getUserId(), path + "/" + name);
        List<File> allFiles = fileService.allFiles(getUserId(), path);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        return "user";
    }

    @PostMapping("/file/delete")
    public String deleteFile(@RequestParam("path") String path, @RequestParam("filename") String filename,
                             Model model, RedirectAttributes attributes) {
        fileService.deleteFile(getUserId(), path + "/" + filename);
        List<File> allFiles = fileService.allFiles(getUserId(), path);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        return "user";
    }

    @GetMapping("/file/get/({path})/{file}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("path") String path,
                                                       @PathVariable("file") String filename) {
        return fileService.getFile(getUser().getId().toString(), path, filename);
    }
}
