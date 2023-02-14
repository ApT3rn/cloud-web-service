package com.leonidov.cloud.service;

import com.leonidov.cloud.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(Long id);
    public boolean save(User user);
}
