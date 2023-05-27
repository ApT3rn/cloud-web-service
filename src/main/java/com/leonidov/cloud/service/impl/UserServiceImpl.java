package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.data.UserRepo;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.model.enums.Role;
import com.leonidov.cloud.model.enums.UserStatus;
import com.leonidov.cloud.service.FileService;
import com.leonidov.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, @Lazy PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        return userRepo.findUserById(id);
    }

    @Override
    public boolean save(User user) {
        if (userRepo.existsById(user.getId()))
            return false;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        fileService.createUserFolder(user.getId().toString());
        userRepo.save(user);
        return true;
    }

    @Override
    public User updateUser(User user) {
        return userRepo.saveAndFlush(user);
    }

    @Override
    public User updatePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.saveAndFlush(user);
        return user;
    }

    @Override
    public boolean deleteUser(User user, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            userRepo.deleteById(user.getId());
            fileService.deleteUserFolder(user.getId().toString());
            return true;
        }
        return false;
    }
}