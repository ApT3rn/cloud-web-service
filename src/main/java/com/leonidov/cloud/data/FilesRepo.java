package com.leonidov.cloud.data;

import com.leonidov.cloud.model.SharedFile;
import com.leonidov.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesRepo extends JpaRepository<SharedFile, String> {
    List<SharedFile> getAllByUser(User user);
    Optional<SharedFile> findById(String id);
    Optional<SharedFile> findByUserAndPathAndName(User user, String path, String name);
}
