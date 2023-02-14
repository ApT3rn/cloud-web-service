package com.leonidov.cloud.auth;

import com.leonidov.cloud.model.User;

public class Mediator {

    private static User USER;

    public Mediator() {}

    public void setUser(User user) {
        Mediator.USER = user;
    }

    public static User getUser() {
        return USER;
    }
}
