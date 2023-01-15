package com.leonidov.cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        String paths = path + File.separator + email + File.separator;

        File[] files = new File(paths).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;

        /*List<File> filesInFolder = null;
        try {
            filesInFolder = Files.walk(Paths.get(paths))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filesInFolder;*/
    }

    public File getFile(String email, String filename) {
        String paths = path + File.separator + email + File.separator;
        File file = new File(paths, filename);
        return file;
    }

    @Override
    public String getUserFolder(String email) {
        return (path.toString() + File.separator + email + File.separator);
    }

    @Override
    public void createFolderForUser(String email, String name) {
        File userPath = new File(path + File.separator + email + File.separator + name);
        if (!userPath.exists()) {
            userPath.mkdirs();
        }
    }
}
