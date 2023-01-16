package com.leonidov.cloud.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService {
    public void createMainFolder();
    public void createUserFolder(String email);
    public List<String> allFiles(String email);
    public String getUserFolder (String email);
    public void createFolderForUser(String email, String name);
    public ResponseEntity<InputStreamResource> getFile(String email, String fileName);
    public void deleteFile (String email, String fileName);
    public void uploadFile (String email, MultipartFile file);
}
