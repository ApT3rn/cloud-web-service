package com.leonidov.cloud.auth;

import com.leonidov.cloud.model.User;

public class Mediator {

    private static User user;

    public Mediator() {
    }

    public void setUser(User user) {
        Mediator.user = user;
    }

    public static User getUser() {
        return user;
    }
}
