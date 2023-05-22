package com.leonidov.cloud.service.impl;

import com.leonidov.cloud.data.UserRepo;
import com.leonidov.cloud.model.User;
import com.leonidov.cloud.model.enums.Role;
import com.leonidov.cloud.model.enums.UserStatus;
import com.leonidov.cloud.service.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    UserRepo userRepo;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    FileService fileService;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    void findUserByEmail() {
        User user = new User("", "", "email", "", Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.doReturn(Optional.of(user)).when(this.userRepo).findUserByEmail("email");
        Optional<User> response = this.userService.findUserByEmail("email");

        assertNotNull(response);
        assertEquals(user, response.get());
        assertEquals("email", response.get().getEmail());
    }

    @Test
    void findUserById() {
        User user = new User(UUID.randomUUID(), "", "",
                "", "", Role.ROLE_USER, UserStatus.DEFAULT, new ArrayList<>());

        Mockito.doReturn(Optional.of(user)).when(this.userRepo).findUserById(user.getId());
        Optional<User> response = this.userService.findUserById(user.getId());

        assertNotNull(response);
        assertEquals(user, response.get());
        assertEquals(user.getId(), response.get().getId());
    }

    @Test
    void save_IfUserExists_ReturnValidResponse() {
        User user = new User("", "", "email", "", Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.doReturn(Optional.of(user)).when(this.userRepo).findUserByEmail("email");
        boolean response = this.userService.save(user);

        assertFalse(response);
    }

    @Test
    void save_IfUserNotExists_ReturnValidResponse() {
        User user = new User(UUID.randomUUID(), "", "",
                "email", "", Role.ROLE_USER, UserStatus.DEFAULT, new ArrayList<>());

        Mockito.when(this.userRepo.findUserByEmail("email")).thenReturn(Optional.empty(), Optional.of(user));
        boolean response = this.userService.save(user);

        assertTrue(response);
    }

    @Test
    void saveAndFlush() {
    }
}