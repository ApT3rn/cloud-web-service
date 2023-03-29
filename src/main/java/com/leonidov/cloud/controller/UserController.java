package com.leonidov.cloud.controller;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

@Controller
public class UserController {

    private final FileService fileService;
    private final UserService userService;

    @Autowired
    public UserController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    private User getUser() {
        return Mediator.getUser();
    }

    private String getUserId() {
        return getUser().getId().toString();
    }

    @GetMapping("user")
    public String userPage(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListAllFiles(getUserId(), "*"));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        return "user2";
    }

    @GetMapping("user/({path})")
    public String userInDirectory(@PathVariable("path") String path, Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListAllFiles(getUserId(), path));
        model.addAttribute("url", "+");
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        return "user2";
    }

    @GetMapping("user/images")
    public String getAllImages(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListFilesForType(
                getUserId(), new HashSet<>(Arrays.asList("jpg", "png", "bmp", "gif", "tif", "psd", "jpeg",
                        "jp2", "tiff", "psd", "raw", "dng"))));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        model.addAttribute("categories", "+");
        return "user2";
    }

    @GetMapping("user/video")
    public String getAllVideo(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListFilesForType(
                getUserId(), new HashSet<>(Arrays.asList("mp4", "avi", "mkv", "wmv", "flv", "mpeg"))));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        model.addAttribute("categories", "+");
        return "user2";
    }

    @GetMapping("user/music")
    public String getAllMusic(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListFilesForType(
                getUserId(), new HashSet<>(Arrays.asList("mp3", "wav", "midi", "aac"))));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        model.addAttribute("categories", "+");
        return "user2";
    }

    @GetMapping("user/docs")
    public String getAllDocs(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.getListFilesForType(
                getUserId(), new HashSet<>(Arrays.asList("doc", "docx", "txt", "pdf", "xls", "xlsx",
                        "ppt", "pptx", "fb2", "epub", "mobi", "djvu"))));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        model.addAttribute("categories", "+");
        return "user2";
    }

    @PostMapping("user/search")
    public String searchFiles(@RequestParam("filename") String filename, Model model) {
        if (filename.isEmpty())
            return "redirect:/user";
        model.addAttribute("user", getUser());
        model.addAttribute("listFiles", fileService.searchFiles(getUserId(), filename));
        model.addAttribute("url", "+");
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(getUserId()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(getUser().getStatus())));
        model.addAttribute("categories", "+");
        return "user2";
    }

    @GetMapping("user/settings")
    public String settingsPage(Model model) {
        model.addAttribute("user", getUser());
        model.addAttribute("url", "+");
        return "settings";
    }

    @PostMapping("user/settings")
    public String settingsPage(User user) {
        userService.saveAndFlush(user);
        return "redirect:/user/settings";
    }
}
