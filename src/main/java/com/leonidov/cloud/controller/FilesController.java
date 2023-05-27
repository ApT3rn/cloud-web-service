package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.MyUserDetails;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;
    private final SharedFileService sharedFileService;
    private final UserService userService;

    private final static String HOME_USER_PAGE = "redirect:/user";
    private final static String USER_PAGE_IN_DIR = "redirect:/user/p/";

    @Autowired
    public FilesController(FileService fileService, SharedFileService sharedFileService, UserService userService) {
        this.fileService = fileService;
        this.sharedFileService = sharedFileService;
        this.userService = userService;
    }

    @GetMapping("download/({path})/{file}")
    public ResponseEntity<?> downloadFile(@PathVariable("path") String path,
                                          @PathVariable("file") String filename,
                                          @AuthenticationPrincipal MyUserDetails userDetails) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fileService.downloadFile(
                        userDetails.getUser().getId().toString(), path, filename));
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
                path + "/" + name);
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
