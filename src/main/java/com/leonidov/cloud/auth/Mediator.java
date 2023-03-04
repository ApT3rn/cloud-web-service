package com.leonidov.cloud.auth;

import com.leonidov.cloud.model.User;
import lombok.Getter;
import lombok.Setter;

public class Mediator {

    @Getter
    @Setter
    private static User user;

    protected Mediator(User user) {
        Mediator.user = user;
    }
}
