package com.leonidov.cloud.dao;

import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilesRepo extends JpaRepository<SharedFile, String> {
    public Optional<SharedFile> findById(String id);
    public Optional<SharedFile> findByUserAndPathAndName(User user, String path, String name);
    public void removeById(String id);
}
