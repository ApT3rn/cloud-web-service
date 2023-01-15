package com.leonidov.cloud.service;

import com.leonidov.cloud.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(Long id);
    public boolean save(User user);
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
