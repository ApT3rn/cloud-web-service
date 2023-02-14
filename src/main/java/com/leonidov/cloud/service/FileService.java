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
    public void createUserFolder(String email);
    public String getUserFolder (String email);
    public void createFolderForUser(String email, String name);
    public List<File> allFiles(String email, String path);
    public ResponseEntity<InputStreamResource> getFile(String email, String path, String fileName);
    public void deleteFile (String email, String fileName);
    public void uploadFile (String email, String path, MultipartFile file);
}
