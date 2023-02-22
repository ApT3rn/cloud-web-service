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
    public boolean createFolderForUser(String id, String name);
    public List<File> getStringListFiles(String id, String path);
    public ResponseEntity<InputStreamResource> downloadFile(String id, String path, String filename);
    public void deleteFile (String id, String filename);
    public void uploadFile (String id, String path, MultipartFile file);
    public boolean renameFile (String id, String path, String filename, String newFilename);
    public String getFileSize(java.io.File file);
    public File search(String id, String filename);
}
