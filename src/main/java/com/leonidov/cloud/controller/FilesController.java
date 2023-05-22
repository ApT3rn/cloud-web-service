package com.leonidov.cloud.controller;

import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

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
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("path") String path,
                                                            @PathVariable("file") String filename,
                                                            Principal principal) {
        return fileService.downloadFile(userService.findUserByEmail(principal.getName()).get().getId().toString(), path, filename);
    }

    @PostMapping("upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("path") String path,
                             Model model, RedirectAttributes attributes,
                             Principal principal) {
        User user = userService.findUserByEmail(principal.getName()).get();
        if (fileService.getUserMemorySizeIsFree(user.getId().toString(), user.getStatus()) > file.getSize()) {
            fileService.uploadFile(user.getId().toString(), path, file);
            attributes.addFlashAttribute("message", "Файл успешно загружен!");
        } else
            attributes.addFlashAttribute("message", "Недостаточно свободного места!");
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }


    @PostMapping("create-folder")
    public String createFolder(@RequestParam("path") String path,
                               @RequestParam("name") String name,
                               Model model, RedirectAttributes attributes,
                               Principal principal) {
        if (!(fileService.createFolderForUser(userService.findUserByEmail(
                principal.getName()).get().getId().toString(), path + "/" + name)))
            attributes.addFlashAttribute("message", "Папка с таким названием уже существует!");
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }

    @PostMapping("delete")
    public String deleteFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             Model model, RedirectAttributes attributes,
                             Principal principal) {
        Optional<User> userFromDb = userService.findUserByEmail(principal.getName());
        String id = sharedFileService.getIdIfFileExists(userFromDb.get(), path, filename);
        if (id.length() > 1)
            sharedFileService.removeSharedFile(id);
        fileService.deleteFile(userFromDb.get().getId().toString(), path + "/" + filename);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }

    @PostMapping("rename")
    public String renameFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             @RequestParam("newFilename") String newFilename,
                             @RequestParam("type") String type,
                             RedirectAttributes attributes, Principal principal) {
        if (fileService.renameFile(
                userService.findUserByEmail(principal.getName()).get().getId().toString(), path, filename, newFilename, type))
            attributes.addFlashAttribute("message", "Успешно переименовано!");
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }

    @PostMapping("shared")
    public String sharedFile(@RequestParam("path") String path,
                             @RequestParam("filename") String filename,
                             @RequestParam("id") String id,
                             Principal principal) {
        if (id.length() < 1)
            sharedFileService.addSharedFile(
                    userService.findUserByEmail(principal.getName()).get(), path, filename);
        else
            sharedFileService.removeSharedFile(id);
        return path.equals("*") ? HOME_USER_PAGE : (USER_PAGE_IN_DIR + path);
    }
}
