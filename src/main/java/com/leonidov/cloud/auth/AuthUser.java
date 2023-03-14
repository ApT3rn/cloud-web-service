package com.leonidov.cloud.auth;

import com.leonidov.cloud.model.User;

import javax.validation.constraints.NotNull;
import java.util.Collections;

public class AuthUser extends org.springframework.security.core.userdetails.User {
    public AuthUser (@NotNull User user) {
        super(user.getEmail(), user.getPassword(), Collections.singleton(user.getRole()));
        new Mediator(user);
    }
}
