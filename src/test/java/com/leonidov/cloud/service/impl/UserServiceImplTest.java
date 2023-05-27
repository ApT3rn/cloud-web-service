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

import java.sql.Time;
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
        User user = new User("", "", "", "",
                Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.doReturn(Optional.of(user)).when(this.userRepo).findUserById(user.getId());
        Optional<User> response = this.userService.findUserById(user.getId());

        assertNotNull(response);
        assertEquals(user, response.get());
        assertEquals(user.getId(), response.get().getId());
    }

    @Test
    void save_IfUserExists_ReturnValidResponse() {
        User user = new User("", "", "email", "", Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.when(this.userRepo.existsById(user.getId())).thenReturn(true);
        boolean response = this.userService.save(user);

        assertFalse(response);
    }

    @Test
    void save_IfUserNotExists_ReturnValidResponse() {
        User user = new User("", "", "email", "",
                Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.when(this.userRepo.existsById(user.getId())).thenReturn(false);
        Mockito.when(this.userRepo.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        boolean response = this.userService.save(user);

        assertTrue(response);
    }

    @Test
    void updateUser_ReturnValidResponse() {
        User user = new User("", "", "", "",
                Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.when(this.userRepo.saveAndFlush(user)).thenReturn(new User(user.getId(), "123", "",
                "1", "", Role.ROLE_USER, UserStatus.DEFAULT, new ArrayList<>()));
        User response = this.userService.updateUser(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertTrue(user.getEmail().length() == 0);
        assertTrue(response.getName().length() == 3);
        assertTrue(response.getEmail().length() == 1);
    }

    @Test
    void updatePassword_ReturnValidResponse() {
        User user = new User("", "",
                "pass", "", Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.when(this.passwordEncoder.encode(user.getPassword())).thenReturn("encode");
        Mockito.when(this.userRepo.saveAndFlush(user)).thenReturn(new User(user.getId(), "", "",
                "", "encode", Role.ROLE_USER, UserStatus.DEFAULT, new ArrayList<>()));
        User response = this.userService.updatePassword(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals("encode", response.getPassword());
        Mockito.verify(this.userRepo, Mockito.times(1)).saveAndFlush(user);
    }

    @Test
    void deleteUserIfEqualsPassword_ReturnValidResponse() {
        String password = "pass";
        User user = new User("", "", "", password, Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.when(this.passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        boolean response = this.userService.deleteUser(user, password);

        Mockito.verify(this.passwordEncoder, Mockito.times(1)).matches(password, user.getPassword());
        assertTrue(response);
    }

    @Test
    void deleteUserIfNotEqualsPassword_ReturnValidResponse() {
        String password = "pass";
        User user = new User("", "", "",
                "password", Role.ROLE_USER, UserStatus.DEFAULT);

        Mockito.when(this.passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
        boolean response = this.userService.deleteUser(user, password);

        Mockito.verify(this.passwordEncoder, Mockito.times(1)).matches(password, user.getPassword());
        assertFalse(response);
    }
}