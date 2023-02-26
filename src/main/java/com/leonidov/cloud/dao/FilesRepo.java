package com.leonidov.cloud.dao;

import com.leonidov.cloud.model.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilesRepo extends JpaRepository<SharedFile, String> {
    public Optional<SharedFile> findById(String id);
}
