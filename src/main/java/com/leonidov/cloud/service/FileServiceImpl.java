package com.leonidov.cloud.service;

import com.leonidov.cloud.model.enums.UserStatus;
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
import java.net.URLConnection;
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

    public FileServiceImpl() {
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
        if (files == null)
            return length;
        for (File file : files) {
            if (file.isFile())
                length += file.length();
            else
                length += getFolderSize(file);
        }
        return length;
    }

    public String getFileSizeInStringUnits(long size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log10(size) / 3);
        double unitValue = 1 << (unitIndex * 10);
        return new DecimalFormat("#,##0.#")
                .format(size / unitValue) + " "
                + units[unitIndex];
    }

    @Override
    public String getFileSize(File file) {
        long size = file.length();
        if (file.isDirectory())
            size = getFolderSize(file);
        if (size == 0)
            return "0 B";
        return getFileSizeInStringUnits(size);
    }

    private String getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");
    }

    private List<com.leonidov.cloud.model.File> addFilesInList(String id, List<File> files, String path) {
        List<com.leonidov.cloud.model.File> result = new ArrayList<>();
        for (File file : files) {
            if (file.isFile())
                result.add(new com.leonidov.cloud.model.File(file.getName(), "true",
                        getFileSize(file), getExtensionByStringHandling(file.getPath()), path, path + "*" + file.getName()));
            else
                result.add(new com.leonidov.cloud.model.File(file.getName(), "false",
                        getFileSize(file), "Папка", path, path + "*" + file.getName()));
        }
        return result.stream()
                .sorted(Comparator.comparing(
                        com.leonidov.cloud.model.File::getIsFile))
                .collect(Collectors.toList());
    }

    @Override
    public List<com.leonidov.cloud.model.File> getListAllFiles(String id, String path) {
        List<com.leonidov.cloud.model.File> result = new ArrayList<>();
        List<File> files = Arrays.stream(Objects.requireNonNull(new File(getUserFolder(id)
                + path.replaceAll("\\*", "/"))
                .listFiles())).collect(Collectors.toList());
        if (files.isEmpty())
            result.add(new com.leonidov.cloud.model.File("", "none", "", "", path, path));
        else
            result = addFilesInList(id, files, path);
        return result;
    }

    @Override
    public List<com.leonidov.cloud.model.File> getListFilesForType(String id, Set<String> types) {
        List<File> files = new ArrayList<>();
        recursiveSearch(new File(getUserFolder(id) + "/"), files, "");
        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.isFile())
                if (types.contains(getExtensionByStringHandling(file.getPath())))
                    result.add(file);
        }
        if (result.isEmpty())
            return new ArrayList<>(Collections.singleton(
                    new com.leonidov.cloud.model.File(
                            "", "empty", "", "", "*", "*")));
        return addFilesInList(id, result, "*");
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
    public long getUserMaxMemory(UserStatus status) {
        switch (status) {
            case LITE:
                return 107_374_182_400L;
            case STANDARD:
                return 536_870_912_000L;
            case PLUS:
                return 1_099_511_627_776L;
            default:
                return 10_737_418_240L;
        }
    }

    public long getUserMemorySizeIsFree(String id, UserStatus status) {
        return (getUserMaxMemory(status)) - getFolderSize(new File(getUserFolder(id)));
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
        List<com.leonidov.cloud.model.File> result = new ArrayList<>();
        List<File> files = new ArrayList<>();
        recursiveSearch(new File(getUserFolder(id) + "/"), files, filename);
        if (files.isEmpty()) {
            result.add(new com.leonidov.cloud.model.File("", "empty", "", "", "*", "*"));
        } else {
            String path = files.get(0).getPath().substring(files.get(0)
                    .getPath().indexOf(id)).substring(id.length());
            result = addFilesInList(id, files, path.substring(0, path.length() - files.get(0)
                    .getName().length()).replace("\\", "*"));
        }
        return result;
    }

    @Override
    public com.leonidov.cloud.model.File getFile(File file, String path) {
        return (new com.leonidov.cloud.model.File(file.getName(), "true",
                getFileSize(file), getExtensionByStringHandling(file.getPath()), path, path + "*" + file.getName()));
    }
}