package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.data.FilesRepo;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.SharedFileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SharedFileServiceImpl implements SharedFileService {

    private FilesRepo filesRepo;
    private FileService fileService;

    @Override
    public String addSharedFile(User user, String path, String filename) {
        Optional<SharedFile> request = filesRepo.findByUserAndPathAndName(user, path, filename);
        if (request.isPresent())
            return request.get().getId();
        SharedFile sharedFile = new SharedFile(path, filename, user);
        filesRepo.save(sharedFile);
        return sharedFile.getId();
    }

    @Override
    public String getIdIfFileExists(User user, String path, String filename) {
        Optional<SharedFile> request = filesRepo.findByUserAndPathAndName(user, path, filename);
        if (request.isPresent())
            return request.get().getId();
        return "";
    }

    @Override
    public void removeSharedFile(String id) {
        filesRepo.deleteById(id);
    }

    @Override
    public SharedFile getSharedFileFromDb(String id) {
        return filesRepo.findById(id).orElse(null);
    }

    @Override
    public List<File> getAllSharedFileForUser(User user) {
        List<SharedFile> sharedFiles = filesRepo.getAllByUser(user);
        if (sharedFiles.isEmpty())
            return Collections.singletonList(new File(
                    "", "empty", "", "", "*", "*", ""));
        List<File> result = new ArrayList<>();
        for (SharedFile sharedFile : sharedFiles) {
            result.add(fileService.convertSharedFileToFile(sharedFile));
        }
        return result;
    }

    @Override
    public List<File> addSharedUrlForFileInListFiles(List<File> files, User user) {
        List<File> result = new ArrayList<>();
        for (File file : files) {
            file.setShare(getIdIfFileExists(user, file.getPath(), file.getName()));
            result.add(file);
        }
        return result;
    }
}
