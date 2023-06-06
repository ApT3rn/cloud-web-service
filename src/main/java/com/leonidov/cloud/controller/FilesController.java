package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.MyUserDetails;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import com.leonidov.cloud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/files")
@AllArgsConstructor
public class FilesController {

    private FileService fileService;
    private SharedFileService sharedFileService;

    private final static String HOME_USER_PAGE = "redirect:/user";
    private final static String USER_PAGE_IN_DIR = "redirect:/user/p/";

    @GetMapping("download/({path})/{file}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("path") String path,
                                          @PathVariable("file") String filename,
                                          @AuthenticationPrincipal MyUserDetails userDetails) {

        Resource resource = null;
        File file = fileService.getFile(
                userDetails.getUser().getId().toString(), path, filename);
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("path") String path,
                             @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        if (fileService.getUserMemorySizeIsFree(user.getId().toString(), user.getStatus()) > file.getSize())
            fileService.uploadFile(user.getId().toString(), path, file);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }


    @PostMapping("create-folder")
    public String createFolder(@RequestParam("path") String path,
                               @RequestParam("name") String name,
                               @AuthenticationPrincipal MyUserDetails userDetails) {
        fileService.createFolderForUser(
                userDetails.getUser().getId().toString(),
                path, name);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }

    @PostMapping("delete")
    public String deleteFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        String id = sharedFileService.getIdIfFileExists(user, path, filename);
        if (id.length() > 1)
            sharedFileService.removeSharedFile(id);
        fileService.deleteFile(user.getId().toString(), path + "/" + filename);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }

    @PostMapping("rename")
    public String renameFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             @RequestParam("newFilename") String newFilename,
                             @RequestParam("type") String type,
                             @AuthenticationPrincipal MyUserDetails userDetails) {
        fileService.renameFile(
                userDetails.getUser().getId().toString(), path, filename, newFilename, type);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }

    @PostMapping("shared")
    public String sharedFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             @RequestParam("id") String id,
                             @AuthenticationPrincipal MyUserDetails userDetails) {
        if (id.length() < 1)
            sharedFileService.addSharedFile(
                    userDetails.getUser(), path, filename);
        else
            sharedFileService.removeSharedFile(id);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }
}
