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
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    private User getUser() {
        return Mediator.getUser();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("path") String path,
                             Model model, RedirectAttributes attributes) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Пожалуйста выберите файл для загрузки");
            return "user-in-folder";
        }
        fileService.uploadFile(getUser().getEmail(), path, file);
        List<File> allFiles = fileService.allFiles(getUser().getEmail(), path);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        attributes.addFlashAttribute("message", "Файл успешно загружен!");
        return "user-in-folder";
    }


    @PostMapping("/createFolder")
    public void createFolder(@RequestParam("path") String path,
                             @RequestParam("name") String name, Model model) {
        fileService.createFolderForUser(getUser().getEmail(), path + "/" + name);
        List<File> allFiles = fileService.allFiles(getUser().getEmail(), path);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
    }

    @PostMapping("/delete/path={path}/file={file}")
    public String deleteFile(@PathVariable("path") String path, @PathVariable("file") String file,
                             Model model, RedirectAttributes attributes) {
        fileService.deleteFile(getUser().getEmail(), path + "/" + file);
        List<File> allFiles = fileService.allFiles(getUser().getEmail(), path);
        model.addAttribute("user", getUser());
        model.addAttribute("allFiles", allFiles);
        if (path.isEmpty())
            return "user";
        return "user-in-folder";
    }

    @GetMapping("/file/get/path={path}/file={file}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("path") String path,
                                                       @PathVariable("file") String filename) {
        return fileService.getFile(getUser().getEmail(), path, filename);
    }
}
