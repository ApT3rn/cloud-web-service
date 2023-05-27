package com.leonidov.cloud.config.security;

import com.leonidov.cloud.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class MyUserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private final User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(user.getRole());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String email) {
        user.setEmail(email);
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public User getUser() {
        return user;
    }
}
