package com.leonidov.cloud.service;

import com.leonidov.cloud.model.File;
import com.leonidov.cloud.model.User;
import org.springframework.stereotype.Service;

@Service
public interface SharedFileService {
    public String addSharedFile(User user, String path, String filename);
    public File getSharedFile(String id);
}
