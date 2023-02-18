package com.leonidov.cloud.dao;

import com.leonidov.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    public Optional<User> findUserByEmail(String email);
    public Optional<User> findUserById(UUID id);
}
