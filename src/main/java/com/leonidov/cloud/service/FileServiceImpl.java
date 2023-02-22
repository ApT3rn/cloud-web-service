package com.leonidov.cloud.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private static final File MAIN_FOLDER = new File(new File("").getAbsolutePath() + "/all_users");

    private FileServiceImpl() {
        createMainFolder();
    }

    @Override
    public void createMainFolder() {
        if (!MAIN_FOLDER.exists())
            MAIN_FOLDER.mkdirs();
    }

    @Override
    public void createUserFolder(String id) {
        File path = new File(MAIN_FOLDER + "/" + id);
        path.mkdirs();
    }

    @Override
    public String getUserFolder(String id) {
        return (MAIN_FOLDER + "/" + id);
    }

    @Override
    public boolean createFolderForUser(String id, String name) {
        File newDirForUser = new File(getUserFolder(id) + "/" + name.replaceAll("\\*", "/"));
        if (newDirForUser.exists())
            return false;
        newDirForUser.mkdirs();
        return true;
    }

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        int count = files.length;
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            }
            else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }

    @Override
    public String getFileSize(File file) {
        long size = file.length();
        if (file.isDirectory()) {
            size = getFolderSize(file);
        }
        if (size == 0)
            return "0 B";
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log10(size) / 3);
        double unitValue = 1 << (unitIndex * 10);
        String readableSize = new DecimalFormat("#,##0.#")
                .format(size / unitValue) + " "
                + units[unitIndex];
        return readableSize;
    }

    @Override
    public List<com.leonidov.cloud.model.File> getStringListFiles(String id, String path) {
        List<com.leonidov.cloud.model.File> results = new ArrayList<>();
        String paths = getUserFolder(id) + path.replaceAll("\\*", "/");
        File[] files = new File(paths).listFiles();
        for (File file : files) {
            if (file.isDirectory())
                results.add(new com.leonidov.cloud.model.File(file.getName(), "false",
                        getFileSize(file), path, path + "*" + file.getName()));
        }
        for (File file : files) {
            if (file.isFile())
                results.add(new com.leonidov.cloud.model.File(file.getName(), "true",
                        getFileSize(file), path, path + "*" + file.getName()));
        }
        if (results.isEmpty()) {
            results.add(new com.leonidov.cloud.model.File("", "none", "", path, path));
        }
        return results;
    }

    public ResponseEntity<InputStreamResource> downloadFile(String id, String path, String filename) {
        String paths = getUserFolder(id) + path.replaceAll("\\*", "/");
        File file = new File(paths, filename);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .contentType(MediaType.TEXT_HTML)
                .contentLength(file.length())
                .body(inputStreamResource);
    }

    @Override
    public void deleteFile(String id, String filename) {
        try {
            Files.walk(Paths.get(getUserFolder(id) + filename.replaceAll("\\*", "/")))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uploadFile(String id, String path, MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Path pathInFolder = Paths.get(
                    getUserFolder(id) + path.replaceAll("\\*", "/") + "/" + fileName);
            Files.copy(file.getInputStream(), pathInFolder, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean renameFile(String id, String path, String filename, String newFilename) {
        File file = new File(getUserFolder(id) + path.replaceAll("\\*", "/") + "/" + filename);
        File newFile = new File(getUserFolder(id) + path.replaceAll("\\*", "/") + "/" + newFilename);
        if (newFile.exists())
            return false;
        file.renameTo(newFile);
        return true;
    }

    @Override
    public com.leonidov.cloud.model.File search(String id, String filename) {
        File path = new File(getUserFolder(id + "/"));
        File[] matchingFiles = path.listFiles((dir, name) -> name.startsWith(filename));
        return new com.leonidov.cloud.model.File("", "", "", "", "");
    }
}