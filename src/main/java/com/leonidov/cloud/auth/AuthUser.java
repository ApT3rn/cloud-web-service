package com.leonidov.cloud.auth;

import com.leonidov.cloud.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.Collections;

public class AuthUser extends org.springframework.security.core.userdetails.User {

    public AuthUser (@NotNull User user) {
        super(user.getEmail(), user.getPassword(), Collections.singleton(user.getRole()));
        new Mediator().setUser(user);
    }
}
