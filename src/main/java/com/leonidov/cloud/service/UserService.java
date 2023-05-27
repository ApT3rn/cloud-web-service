package com.leonidov.cloud.service;

import com.leonidov.cloud.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    public Optional<User> findUserByEmail(String email);
    public Optional<User> findUserById(UUID id);
    public boolean save(User user);
    public User updateUser(User user);
    public User updatePassword(User user);
    public boolean deleteUser(User user, String password);
}
