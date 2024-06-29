package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void When_Save_WithExistingUser_ShouldThrowIllegalArgumentException() {
        UserModel existingUser = new UserModel();
        existingUser.setName("existingUser");
        existingUser.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingUser));

        UserModel userModel = new UserModel();
        userModel.setName("test");
        userModel.setEmail("test@test.com");
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> userServiceImpl.save(userModel));
        assertEquals("User already exists for the given email.", ex.getMessage());
    }

    @Test
    void When_Login_WithInvalidEmailOrPassword_ShouldThrowAuthenticationException() {
        when(userRepository.findUserByEmailAndPassword("test@test.com", "password")).thenReturn(Optional.empty());
        AuthenticationException ex = assertThrows(
                AuthenticationException.class, () -> userServiceImpl.login(
                        "test@test.com", "password"));
        assertEquals("Invalid email or password.", ex.getMessage());
    }

    @Test
    void When_Login_WithValidEmailOrPassword_ShouldSucceed() throws AuthenticationException {
        UserModel userModel = new UserModel();
        userModel.setEmail("test@test.com");
        userModel.setPassword("password");
        when(userRepository.findUserByEmailAndPassword(
                "test@test.com", "password")).thenReturn(Optional.of(userModel));
        UUID token = userServiceImpl.login("test@test.com", "password");
        assertNotNull(token);
    }
}