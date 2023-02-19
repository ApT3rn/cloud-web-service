package com.leonidov.cloud.service;

import com.leonidov.cloud.model.User;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    public Optional<User> findUserByEmail(String email);
    public Optional<User> findUserById(UUID id);
    public boolean save(User user);
    public boolean saveAndFlush(@NotNull User user);
}
