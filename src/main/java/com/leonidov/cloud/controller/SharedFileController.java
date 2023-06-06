package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/file")
@AllArgsConstructor
public class SharedFileController {

    private SharedFileService sharedFileService;
    private FileService fileService;
    @Value("${project.url}")
    private static String projectURL;

    @GetMapping("{id}")
    public String getFile(@PathVariable("id") String id, Model model) {
        SharedFile file = sharedFileService.getSharedFileFromDb(id);
        if (file == null) return "redirect:/";
        model.addAttribute("file", file);
        model.addAttribute("url", ("/file/" + file.getId() + "/download"));
        return "file";
    }

    @GetMapping("{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable("id") String id) {
        SharedFile sharedFile = sharedFileService.getSharedFileFromDb(id);
        if (sharedFile == null) return ResponseEntity.notFound().build();

        File file = fileService.getFile(
                sharedFile.getUser().getId().toString(),
                sharedFile.getPath(), sharedFile.getName());

        Resource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
