package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/files")
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

    @GetMapping("download/({path})/{file}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("path") String path,
                                                            @PathVariable("file") String filename) {
        return fileService.downloadFile(getUser().getId().toString(), path, filename);
    }

    @PostMapping("upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("path") String path,
                             Model model, RedirectAttributes attributes) {
        fileService.uploadFile(getUserId(), path, file);
        attributes.addFlashAttribute("message", "Файл успешно загружен!");
        if (path.equals("*"))
            return "redirect:/user";
        return ("redirect:/user/" + path);
    }


    @PostMapping("create-folder")
    public String createFolder(@RequestParam("path") String path,
                               @RequestParam("name") String name,
                               Model model, RedirectAttributes attributes) {
        boolean response = fileService.createFolderForUser(getUserId(), path + "/" + name);
        if (!response) {
            attributes.addFlashAttribute("message", "Папка с таким названием уже существует!");
        }
        if (path.equals("*"))
            return "redirect:/user";
        return ("redirect:/user/" + path);
    }

    @PostMapping("delete")
    public String deleteFile(@RequestParam("path") String path, @RequestParam("filename") String filename,
                             Model model, RedirectAttributes attributes) {
        fileService.deleteFile(getUserId(), path + "/" + filename);
        if (path.equals("*"))
            return "redirect:/user";
        return ("redirect:/user/" + path);
    }

    @PostMapping("rename")
    public String renameFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             @RequestParam("newFilename") String newFilename,
                             Model model, RedirectAttributes attributes) {
        boolean response = fileService.renameFile(getUserId(), path, filename, newFilename);
        if (!response) {
            attributes.addFlashAttribute("message", "Файл с таким названием уже существует!");
        }
        if (path.equals("*"))
            return "redirect:/user";
        return ("redirect:/user/" + path);
    }
}
