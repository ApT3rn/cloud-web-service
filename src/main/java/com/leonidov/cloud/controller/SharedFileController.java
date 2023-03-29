package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.File;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/file")
public class SharedFileController {

    private final SharedFileService sharedFileService;
    private final FileService fileService;

    @Autowired
    public SharedFileController(SharedFileService sharedFileService, FileService fileService) {
        this.sharedFileService = sharedFileService;
        this.fileService = fileService;
    }

    @GetMapping("{id}")
    public String getFile(@PathVariable("id") String id, Model model) {
        Optional<File> file = Optional.ofNullable(sharedFileService.getFile(id));
        if (file.isPresent()) {
            model.addAttribute("file", file);
            return "file";
        }
        return "404";
    }

    @GetMapping("{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable("id") String id) {
        Optional<File> file = Optional.ofNullable(sharedFileService.getFile(id));
        if (file.isPresent())
            return fileService.downloadFile(id, file.get().getPath(), file.get().getName());
        return new ResponseEntity<>("404", HttpStatus.NO_CONTENT);
    }
}
