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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        new File(MAIN_FOLDER + "/" + id).mkdirs();
    }

    @Override
    public String getUserFolder(String id) {
        return (MAIN_FOLDER + "/" + id);
    }

    @Override
    public boolean createFolderForUser(String id, String name) {
        return new File(getUserFolder(id) + "/" + name.replaceAll("\\*", "/")).mkdirs();
    }

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        int count = files.length;
        for (File file : files) {
            if (file.isFile())
                length += file.length();
            else
                length += getFolderSize(file);
        }
        return length;
    }

    @Override
    public String getFileSize(File file) {
        long size = file.length();
        if (file.isDirectory())
            size = getFolderSize(file);
        if (size == 0)
            return "0 B";
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log10(size) / 3);
        double unitValue = 1 << (unitIndex * 10);
        return new DecimalFormat("#,##0.#")
                .format(size / unitValue) + " "
                + units[unitIndex];
    }

    @Override
    public List<com.leonidov.cloud.model.File> getStringListFiles(String id, String path) {
        List<com.leonidov.cloud.model.File> results = new ArrayList<>();
        List<File> files = Arrays.stream(Objects.requireNonNull(new File(getUserFolder(id)
                + path.replaceAll("\\*", "/"))
                .listFiles())).collect(Collectors.toList());
        if (files.isEmpty()) {
            results.add(new com.leonidov.cloud.model.File("", "none", "", path, path));
        } else {
            for (File file : files) {
                if (file.isDirectory())
                    results.add(new com.leonidov.cloud.model.File(file.getName(), "false",
                            getFileSize(file), path, path + "*" + file.getName()));
                if (file.isFile())
                    results.add(new com.leonidov.cloud.model.File(file.getName(), "true",
                            getFileSize(file), path, path + "*" + file.getName()));
            }
            results = results.stream()
                    .sorted(Comparator.comparing(
                            com.leonidov.cloud.model.File::getIsFile))
                    .collect(Collectors.toList());
        }
        return results;
    }

    public ResponseEntity<InputStreamResource> downloadFile(String id, String path, String filename) {
        File file = new File(getUserFolder(id) + path.replaceAll("\\*", "/"), filename);
        InputStreamResource inputStreamResource = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            inputStreamResource = new InputStreamResource(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }

    @Override
    public void uploadFile(String id, String path, MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Files.copy(file.getInputStream(), Paths.get(getUserFolder(id) +
                            path.replaceAll("\\*", "/") + "/" + fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean renameFile(String id, String path, String filename, String newFilename) {
        File file = new File(getUserFolder(id) +
                path.replaceAll("\\*", "/") + "/" + filename);
        File newFile = new File(getUserFolder(id) +
                path.replaceAll("\\*", "/") + "/" + newFilename);
        return file.renameTo(newFile);
    }

    private void recursiveSearch(File rootFile, List<File> fileList, String filename) {
        File[] directoryFiles = rootFile.listFiles();
        if (directoryFiles != null)
            for (File file : directoryFiles) {
                if (file.isDirectory()) {
                    if (file.getName().toLowerCase().startsWith(filename.toLowerCase()))
                        fileList.add(file);
                    recursiveSearch(file, fileList, filename);
                } else if (file.getName().toLowerCase().startsWith(filename.toLowerCase()))
                    fileList.add(file);
            }
    }

    @Override
    public List<com.leonidov.cloud.model.File> searchFiles(String id, String filename) {
        List<File> files = new ArrayList<>();
        recursiveSearch(new File(getUserFolder(id) + "/"), files, filename);
        List<com.leonidov.cloud.model.File> results = new ArrayList<>();
        if (files.isEmpty()) {
            results.add(new com.leonidov.cloud.model.File("", "empty", "", "*", "*"));
        } else {
            for (File file : files) {
                String path = file.getPath().substring(file.getPath().indexOf(id)).substring(id.length());
                path = path.substring(0, path.length() - file.getName().length()).replace("\\", "*");
                if (file.isDirectory())
                    results.add(new com.leonidov.cloud.model.File(file.getName(), "false",
                            getFileSize(file), path,
                            path + "*" + file.getName()));
                if (file.isFile())
                    results.add(new com.leonidov.cloud.model.File(file.getName(), "true",
                        getFileSize(file), path,
                        path + "*" + file.getName()));
            }
            results = results.stream()
                    .sorted(Comparator.comparing(
                            com.leonidov.cloud.model.File::getIsFile))
                    .collect(Collectors.toList());
        }
        return results;
    }

    @Override
    public com.leonidov.cloud.model.File getFile(File file, String path) {
        return (new com.leonidov.cloud.model.File(file.getName(), "true",
                getFileSize(file), path, path + "*" + file.getName()));
    }
}