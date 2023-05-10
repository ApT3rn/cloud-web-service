package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;

@Controller
public class UserController {

    private final FileService fileService;
    private final UserService userService;
    private final SharedFileService sharedFileService;

    @Autowired
    public UserController(FileService fileService, UserService userService, SharedFileService sharedFileService) {
        this.fileService = fileService;
        this.userService = userService;
        this.sharedFileService = sharedFileService;
    }

    @GetMapping("user")
    public String userPage(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListAllFiles(user.getId().toString(), "*"), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        return "user";
    }

    @GetMapping("user/p/{path}")
    public String userInDirectory(@PathVariable("path") String path, Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListAllFiles(user.getId().toString(), path), user));
        model.addAttribute("url", "+");
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        return "user";
    }

    @GetMapping("user/images")
    public String getAllImages(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListFilesForType(
                user.getId().toString(), new HashSet<>(Arrays.asList("jpg", "png", "bmp", "gif", "tif", "psd", "jpeg",
                        "jp2", "tiff", "psd", "raw", "dng"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/video")
    public String getAllVideo(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListFilesForType(
                user.getId().toString(), new HashSet<>(Arrays.asList(
                        "mp4", "avi", "mkv", "wmv", "flv", "mpeg"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/music")
    public String getAllMusic(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(fileService.getListFilesForType(
                user.getId().toString(), new HashSet<>(
                        Arrays.asList("mp3", "wav", "midi", "aac"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/docs")
    public String getAllDocs(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(fileService.getListFilesForType(
                user.getId().toString(), new HashSet<>(
                        Arrays.asList("doc", "docx", "txt", "pdf", "xls", "xlsx",
                        "ppt", "pptx", "fb2", "epub", "mobi", "djvu"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/shared")
    public String sharedFiles(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.getAllSharedFileForUser(user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "user";
    }

    @PostMapping("user/search")
    public String searchFiles(@RequestParam("filename") String filename, Model model, Principal principal) {
        if (filename.isEmpty())
            return "redirect:/user";
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.searchFiles(user.getId().toString(), filename), user));
        model.addAttribute("url", "+");
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/settings")
    public String settingsPage(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getFileSizeInStringUnits(fileService.getUserMaxMemory(user.getStatus())));
        model.addAttribute("categories", "+");
        return "settings";
    }

    @PostMapping("user/settings")
    public String settingsPage(User user) {
        userService.saveAndFlush(user);
        return "redirect:/user/settings";
    }
}
