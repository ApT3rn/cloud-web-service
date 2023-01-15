package com.leonidov.cloud.dao;

import com.leonidov.cloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    public Optional<User> getUserByEmail(String email);
    public Optional<User> getUserById(Long id);
}
