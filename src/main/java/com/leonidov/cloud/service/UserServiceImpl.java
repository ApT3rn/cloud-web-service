package com.leonidov.cloud.service;

import com.leonidov.cloud.auth.Mediator;
import com.leonidov.cloud.dao.UserRepo;
import com.leonidov.cloud.model.enums.Role;
import com.leonidov.cloud.model.enums.UserStatus;
import com.leonidov.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
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

    public boolean save(User user) {
        if (userRepo.findUserByEmail(user.getEmail()).isPresent())
            return false;
        user.setRole(Role.valueOf(Role.ROLE_USER.toString()));
        user.setStatus(UserStatus.valueOf(UserStatus.DEFAULT.toString()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        fileService.createUserFolder(userRepo.findUserByEmail(user.getEmail()).get().getId().toString());
        return true;
    }

    public boolean saveAndFlush(@NotNull User user) {
        if (userRepo.findUserById(user.getId()).isPresent()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.saveAndFlush(user);
            Mediator.setUser(userRepo.findUserById(user.getId()).get());
            return true;
        }
        return false;
    }
}