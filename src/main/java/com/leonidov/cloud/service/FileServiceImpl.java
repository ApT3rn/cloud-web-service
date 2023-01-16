package com.leonidov.cloud.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private static final File path = new File(new File("").getAbsolutePath() + File.separator + "all_users");

    public FileServiceImpl() {
    }

    @Override
    public void createMainFolder() {
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    @Override
    public void createUserFolder(String email) {
        File paths = new File(path + File.separator + email);
        paths.mkdirs();
    }

    @Override
    public List<String> allFiles(String email) {
        List<String> results = new ArrayList<>();
        String paths = getUserFolder(email) + File.separator;

        File[] files = new File(paths).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;
    }

    public ResponseEntity<InputStreamResource> getFile(String email, String fileName) {
        String paths = getUserFolder(email) + File.separator;
        File file = new File(paths, fileName);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.TEXT_HTML)
                .contentLength(file.length())
                .body(inputStreamResource);
    }

    @Override
    public String getUserFolder(String email) {
        return (path.toString() + File.separator + email + File.separator);
    }

    @Override
    public void createFolderForUser(String email, String name) {
        File userPath = new File(getUserFolder(email) + File.separator + name);
        if (!userPath.exists()) {
            userPath.mkdirs();
        }
    }

    @Override
    public void deleteFile(String email, String fileName) {
        try {
            Path path = Paths.get(
                    getUserFolder(email + File.separator + fileName));
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadFile(String email, MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Path path = Paths.get(
                    getUserFolder(email) + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
