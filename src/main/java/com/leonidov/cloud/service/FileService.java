package com.leonidov.cloud.service;

import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.enums.UserStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface FileService {
    public void createMainFolder();
    public void createUserFolder(String id);
    public String getUserFolder (String id);
    public boolean createFolderForUser(String id, String name);
    public String getFileSizeInStringUnits(long size);
    public List<File> getListAllFiles(String id, String path);
    public List<File> getListFilesForType(String id, Set<String> types);
    public ResponseEntity<InputStreamResource> downloadFile(String id, String path, String filename);
    public void deleteFile (String id, String filename);
    public void uploadFile (String id, String path, MultipartFile file);
    public boolean renameFile (String id, String path, String filename, String newFilename, String type);
    public String getFileSize(java.io.File file);
    public List<File> searchFiles(String id, String filename);
    public File getFile(java.io.File file, String path, String share);
    public long getUserMemorySizeIsFree(String id, UserStatus status);
    public long getUserMaxMemory(UserStatus status);
    public String getUserMaxMemoryInStringUnits(UserStatus status);
}
