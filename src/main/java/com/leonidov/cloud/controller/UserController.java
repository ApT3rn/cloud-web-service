package com.leonidov.cloud.controller;

import com.leonidov.cloud.config.security.MyUserDetails;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
    public String userPage(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListAllFiles(user.getId().toString(), "*"), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        return "user";
    }

    @GetMapping("user/p/{path}")
    public String userInDirectory(@PathVariable("path") String path, Model model,
                                  @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListAllFiles(user.getId().toString(), path), user));
        model.addAttribute("url", "+");
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        return "user";
    }

    @GetMapping("user/images")
    public String getAllImages(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListFilesForType(
                        user.getId().toString(), new HashSet<>(Arrays.asList("jpg", "png", "bmp", "gif", "tif", "psd", "jpeg",
                                "jp2", "tiff", "psd", "raw", "dng"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/video")
    public String getAllVideo(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.getListFilesForType(
                        user.getId().toString(), new HashSet<>(Arrays.asList(
                                "mp4", "avi", "mkv", "wmv", "flv", "mpeg"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/music")
    public String getAllMusic(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(fileService.getListFilesForType(
                user.getId().toString(), new HashSet<>(
                        Arrays.asList("mp3", "wav", "midi", "aac"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/docs")
    public String getAllDocs(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(fileService.getListFilesForType(
                user.getId().toString(), new HashSet<>(
                        Arrays.asList("doc", "docx", "txt", "pdf", "xls", "xlsx",
                                "ppt", "pptx", "fb2", "epub", "mobi", "djvu"))), user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/shared")
    public String sharedFiles(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.getAllSharedFileForUser(user));
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "user";
    }

    @PostMapping("user/search")
    public String searchFiles(@RequestParam("filename") String filename, Model model,
                              @AuthenticationPrincipal MyUserDetails userDetails) {
        if (filename.isEmpty())
            return "redirect:/user";
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("listFiles", sharedFileService.addSharedUrlForFileInListFiles(
                fileService.searchFiles(user.getId().toString(), filename), user));
        model.addAttribute("url", "+");
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "user";
    }

    @GetMapping("user/settings")
    public String settingsPage(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("memory", "Занято " +
                fileService.getFileSize(new File(fileService.getUserFolder(user.getId().toString()))) + " из " +
                fileService.getUserMaxMemoryInStringUnits(user.getStatus()));
        model.addAttribute("categories", "+");
        return "settings";
    }

    @PostMapping("user/settings/update")
    public String updateUser(@RequestParam String name,
                             @RequestParam String surname,
                             @RequestParam String email,
                             @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        User tempUser = userService.updateUser(user);
        userDetails.setUsername(tempUser.getEmail());
        return "redirect:/user/settings";
    }

    @PostMapping("user/settings/change-password")
    public String changePassword(@RequestParam String password,
                                 @AuthenticationPrincipal MyUserDetails userDetails) {
        User user = userDetails.getUser();
        user.setPassword(password);
        User tempUser = userService.updatePassword(user);
        userDetails.setPassword(tempUser.getPassword());
        return "redirect:/user/settings";
    }

    @PostMapping("user/settings/delete")
    public String deleteUser(@RequestParam String password,
                             @AuthenticationPrincipal MyUserDetails userDetails,
                             Authentication authentication,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        User user = userDetails.getUser();
        if (userService.deleteUser(user, password)) {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            return "redirect:/";
        }
        return "redirect:/user/settings";
    }
}
