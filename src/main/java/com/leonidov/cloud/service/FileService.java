package com.leonidov.cloud.service;

import com.leonidov.cloud.model.File;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService {
    public void createMainFolder();
    public void createUserFolder(String id);
    public String getUserFolder (String id);
    public void createFolderForUser(String id, String name);
    public List<File> allFiles(String id, String path);
    public ResponseEntity<InputStreamResource> getFile(String id, String path, String fileName);
    public void deleteFile (String id, String fileName);
    public void uploadFile (String id, String path, MultipartFile file);
}
