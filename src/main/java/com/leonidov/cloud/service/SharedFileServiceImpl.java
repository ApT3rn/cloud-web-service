package com.leonidov.cloud.service;

import com.leonidov.cloud.dao.FilesRepo;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SharedFileServiceImpl implements SharedFileService {

    private final FilesRepo filesRepo;
    private final FileService fileService;

    @Autowired
    public SharedFileServiceImpl(FilesRepo filesRepo, FileService fileService) {
        this.filesRepo = filesRepo;
        this.fileService = fileService;
    }

    @Override
    public String addSharedFile(User user, String path, String filename) {
        if (filesRepo.findByUserAndPathAndName(user, path, filename).isPresent())
            return filesRepo.findByUserAndPathAndName(user, path, filename).get().getId();
        filesRepo.save(new SharedFile(path, filename, user));
        return filesRepo.findByUserAndPathAndName(user, path, filename).get().getId();
    }

    @Override
    public String getIdIfFileExists(User user, String path, String filename) {
        if (filesRepo.findByUserAndPathAndName(user, path, filename).isPresent())
            return filesRepo.findByUserAndPathAndName(user, path, filename).get().getId();
        return "";
    }

    @Override
    public void removeSharedFile(String id) {
        filesRepo.deleteById(id);
    }

    @Override
    public File getFile(String id) {
        Optional<SharedFile> sharedFile = filesRepo.findById(id);
        if (sharedFile.isPresent()) {
            java.io.File file = new java.io.File(sharedFile.get().getUser().getId().toString() +
                    sharedFile.get().getPath().replaceAll(
                            "\\*", "/") + "/" + sharedFile.get().getName());
            return fileService.getFile(file, sharedFile.get().getPath(), id);
        }
        return null;
    }

    @Override
    public List<File> getAllSharedFileForUser(User user) {
        List<SharedFile> sharedFiles = filesRepo.getAllByUser(user);
        if (sharedFiles.isEmpty())
            return Collections.singletonList(new File(
                    "", "empty", "", "", "*", "*", ""));
        List<File> result = new ArrayList<>();
        for (SharedFile sharedFile : sharedFiles) {
            result.add(fileService.getFile(new java.io.File((fileService.getUserFolder(String.valueOf(user.getId())) + "/" +
                            sharedFile.getPath() + "/" + sharedFile.getName()).replaceAll("\\*", "/")),
                    sharedFile.getPath().replaceAll("\\*", "/"), sharedFile.getId()));
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
