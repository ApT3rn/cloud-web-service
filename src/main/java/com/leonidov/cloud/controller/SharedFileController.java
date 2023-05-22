package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        Optional<SharedFile> file = Optional.ofNullable(sharedFileService.getFile(id));
        if (file.isPresent()) {
            model.addAttribute("file", file.get());
            model.addAttribute("url",  ("http://datasky.space/file/" + id + "/download"));
            return "file";
        }
        return "redirect:/";
    }

    @GetMapping("{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable("id") String id) {
        Optional<SharedFile> file = Optional.ofNullable(sharedFileService.getFile(id));
        System.out.println(file.get());
        return file.isPresent() ?
                fileService.downloadFile(file.get().getUser().getId().toString(), file.get().getPath(), file.get().getName())
                : ResponseEntity.notFound().build();
    }
}
