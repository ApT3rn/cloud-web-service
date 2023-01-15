package com.leonidov.cloud.service;

import com.leonidov.cloud.auth.AuthUser;
import com.leonidov.cloud.dao.UserRepo;
import com.leonidov.cloud.model.Role;
import com.leonidov.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepo.getUserById(id);
    }

    public boolean save(User user) {
        fileService.createMainFolder();
        Optional<User> o = userRepo.getUserByEmail(user.getEmail());
        if (o.isPresent()) {
            return false;
        } else {
            user.setRole(Role.valueOf(Role.ROLE_USER.toString()));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            fileService.createUserFolder(user.getEmail());
            userRepo.save(user);
            return true;
        }
    }

    public boolean flush(@NotNull User user) {
        Optional<User> o = userRepo.getUserByEmail(user.getEmail());
        if (o.isPresent()) {
            userRepo.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> o = userRepo.getUserByEmail(email);
        if (o.isPresent()) {
            return new AuthUser(o.get());
        } else {
            throw new UsernameNotFoundException(format("Пользователь: %s, не найден", email));
        }
    }
}
