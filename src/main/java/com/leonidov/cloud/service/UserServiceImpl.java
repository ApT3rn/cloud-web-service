package com.leonidov.cloud.service;

import com.leonidov.cloud.dao.UserRepo;
import com.leonidov.cloud.model.Role;
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
        Optional<User> o = userRepo.findUserByEmail(user.getEmail());
        if (o.isPresent())
            return false;
        else {
            user.setRole(Role.valueOf(Role.ROLE_USER.toString()));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
            fileService.createUserFolder(userRepo.findUserByEmail(user.getEmail()).get().getId().toString());
            return true;
        }
    }

    public boolean flush(@NotNull User user) {
        Optional<User> o = userRepo.findUserByEmail(user.getEmail());
        if (o.isPresent()) {
            userRepo.saveAndFlush(user);
            return true;
        } else
            return false;
    }
}
