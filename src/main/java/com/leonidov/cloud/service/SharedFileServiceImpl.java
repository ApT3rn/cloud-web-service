package com.leonidov.cloud.service;

import com.leonidov.cloud.dao.FilesRepo;
import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SharedFileServiceImpl implements SharedFileService {

    private final FilesRepo filesRepo;
    private final FileService fileService;

    @Autowired
    private SharedFileServiceImpl(FilesRepo filesRepo, FileService fileService) {
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
    public String findByUserAndPathAndName(User user, String path, String filename) {
        Optional<SharedFile> file = filesRepo.findByUserAndPathAndName(user, path, filename);
        if (file.isPresent())
            return file.get().getId();
        return "";
    }

    @Override
    public void removeSharedFile(String id) {
        filesRepo.removeSharedFileById(id);
    }

    @Override
    public File getSharedFile(String id) {
        Optional<SharedFile> sharedFile = filesRepo.findById(id);
        if (sharedFile.isPresent()) {
            java.io.File file = new java.io.File(sharedFile.get().getUser().getId().toString() +
                    sharedFile.get().getPath().replaceAll(
                            "\\*", "/") + "/" + sharedFile.get().getName());
            return fileService.getFile(file, sharedFile.get().getPath());
        }
        return null;
    }
}
