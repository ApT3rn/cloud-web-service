package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.enums.UserStatus;
import com.leonidov.cloud.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    static final File MAIN_FOLDER = new File(new File("").getAbsolutePath() + "/all_users");

    public FileServiceImpl() {
        createMainFolder();
    }

    @Override
    public void createMainFolder() {
        if (!MAIN_FOLDER.exists())
            MAIN_FOLDER.mkdirs();
    }

    @Override
    public boolean createUserFolder(String id) {
        return new File(MAIN_FOLDER + "/" + id).mkdir();
    }

    @Override
    public boolean deleteUserFolder(String id) {
        try {
            Files.walk(Paths.get(MAIN_FOLDER + "/" + id + "/"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUserFolder(String id) {
        return (MAIN_FOLDER + "/" + id);
    }

    @Override
    public boolean createFolderForUser(String id, String path, String name) {
        File newDir = getFile(id, path, name);
        if (newDir.exists())
            return false;
        return newDir.mkdirs();
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

    @Override
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
                        getFileSize(file), getExtensionByStringHandling(file.getPath()), path, path + "*" + file.getName(), ""));
            else
                result.add(new com.leonidov.cloud.model.File(file.getName(), "false",
                        getFileSize(file), "Папка", path, path + "*" + file.getName(), ""));
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
            result.add(new com.leonidov.cloud.model.File("", "none", "", "", path, path, ""));
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
        return result.isEmpty() ? new ArrayList<>(Collections.singleton(
                new com.leonidov.cloud.model.File(
                        "", "empty", "", "", "*", "*", "")))
                : addFilesInList(id, result, "*");
    }

    @Override
    public File getFile(String id, String path, String filename) {
        return new File(getUserFolder(id) + path.replaceAll("\\*", "/"), filename);
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
            case STANDART:
                return 536_870_912_000L;
            case PLUS:
                return 1_073_741_824_000L;
            default:
                return 107_374_182_40L;
        }
    }

    @Override
    public String getUserMaxMemoryInStringUnits(UserStatus status) {
        switch (status) {
            case LITE:
                return "100 GB";
            case STANDART:
                return "500 GB";
            case PLUS:
                return "1 TB";
            default:
                return "10 GB";
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
    public void renameFile(String id, String path, String filename, String newFilename, String type) {
        File file = getFile(id, path, filename);
        File newFile;
        if (type.equals("Папка"))
            newFile = getFile(id, path, newFilename);
        else
            newFile = getFile(id, path, newFilename + "." + type);
        file.renameTo(newFile);
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
            result.add(new com.leonidov.cloud.model.File("", "empty", "", "", "*", "*", ""));
        } else {
            String path = files.get(0).getPath().substring(files.get(0)
                    .getPath().indexOf(id)).substring(id.length());
            result = addFilesInList(id, files, path.substring(0, path.length() - files.get(0)
                    .getName().length()).replace("\\", "*"));
        }
        return result;
    }

    public com.leonidov.cloud.model.File convertSharedFileToFile(SharedFile sharedFile) {
        File file = getFile(sharedFile.getUser().getId().toString(),
                sharedFile.getPath(), sharedFile.getName());

        return new com.leonidov.cloud.model.File(file.getName(), "true",
                getFileSize(file), getExtensionByStringHandling(file.getPath()),
                sharedFile.getPath().replaceAll("/", "\\*"),
                sharedFile.getPath().replaceAll("/", "\\*") + "*" + file.getName(), sharedFile.getId());
    }
}