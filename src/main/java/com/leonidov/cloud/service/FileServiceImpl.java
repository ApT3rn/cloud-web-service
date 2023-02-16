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

    private static final File MAIN_FOLDER = new File(new File("").getAbsolutePath() + "/all_users");

    public FileServiceImpl() {
        createMainFolder();
    }

    @Override
    public void createMainFolder() {
        if (!MAIN_FOLDER.exists())
            MAIN_FOLDER.mkdirs();
    }

    @Override
    public void createUserFolder(String email) {
        File path = new File(MAIN_FOLDER + "/" + email);
        path.mkdirs();
    }

    @Override
    public String getUserFolder(String email) {
        return (MAIN_FOLDER + "/" + email + "/");
    }

    @Override
    public void createFolderForUser(String email, String name) {
        File userPath = new File(getUserFolder(email) + name.replaceAll("\\*", "/"));
        if (!userPath.exists())
            userPath.mkdirs();
    }

    @Override
    public List<com.leonidov.cloud.model.File> allFiles(String email, String path) {
        List<com.leonidov.cloud.model.File> results = new ArrayList<>();
        String paths = getUserFolder(email) + path.replaceAll("\\*", "/") + "/";
        File[] files = new File(paths).listFiles();
        for (File file : files) {
            if (file.isDirectory())
                results.add(new com.leonidov.cloud.model.File(file.getName(), "false",
                        file.getUsableSpace(), path, path + "*" + file.getName()));
            else
                results.add(new com.leonidov.cloud.model.File(file.getName(), "true",
                        file.getUsableSpace(), path, path + "*" + file.getName()));
        }
        if (results.isEmpty())
            results.add(new com.leonidov.cloud.model.File("", "none", 0, path, path));
        return results;
    }

    public ResponseEntity<InputStreamResource> getFile(String email, String path, String fileName) {
        String paths = getUserFolder(email) + "/" + path.replaceAll("\\*", "/");
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
    public void deleteFile(String email, String fileName) {
        try {
            Path path = Paths.get(
                    getUserFolder(email) + fileName.replaceAll("\\*", "/"));
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadFile(String email, String path, MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Path pathInFolder = Paths.get(
                    getUserFolder(email) + path.replaceAll("\\*", "/") + "/" + fileName);
            Files.copy(file.getInputStream(), pathInFolder, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
