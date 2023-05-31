package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    @Value("${project.url}")
    private static String projectURL;

    @Autowired
    public SharedFileController(SharedFileService sharedFileService, FileService fileService) {
        this.sharedFileService = sharedFileService;
        this.fileService = fileService;
    }

    @GetMapping("{id}")
    public String getFile(@PathVariable("id") String id, Model model) {
        Optional<SharedFile> file = Optional.ofNullable(sharedFileService.getSharedFileFromDb(id));
        if (file.isPresent()) {
            model.addAttribute("file", file.get());
            model.addAttribute("url",  (projectURL + "file/" + id + "/download"));
            return "file";
        }
        return "redirect:/";
    }

    @GetMapping("{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable("id") String id) {
        Optional<SharedFile> file = Optional.ofNullable(sharedFileService.getSharedFileFromDb(id));
        if (!file.isPresent())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fileService.downloadFile(
                        file.get().getUser().getId().toString(),
                        file.get().getPath(), file.get().getName()));
    }
}
