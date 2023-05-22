package com.leonidov.cloud.service;

import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SharedFileService {
    public String addSharedFile(User user, String path, String filename);
    public void removeSharedFile(String id);
    public SharedFile getFile(String id);
    public String getIdIfFileExists(User user, String path, String filename);
    public List<File> getAllSharedFileForUser(User user);
    public List<File> addSharedUrlForFileInListFiles(List<File> files, User user);
}
