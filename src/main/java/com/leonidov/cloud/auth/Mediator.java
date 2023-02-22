package com.leonidov.cloud.auth;

import com.leonidov.cloud.model.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Mediator {

    private static User user;

    protected Mediator(User user) {
        Mediator.user = user;
    }

    public static User getUser() {
        return user;
    }
}
