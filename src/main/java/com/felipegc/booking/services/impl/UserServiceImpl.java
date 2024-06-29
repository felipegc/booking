package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.repositories.UserRepository;
import com.felipegc.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserModel save(UserModel userModel) {
        Optional<UserModel> existingUserModel = userRepository.findByEmail(userModel.getEmail());
        if (existingUserModel.isPresent()) {
            throw new IllegalArgumentException("User already exists for the given email.");
        }
        return userRepository.save(userModel);
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UUID login(String email, String password) throws AuthenticationException {
        Optional<UserModel> userByEmailAndPassword = userRepository.findUserByEmailAndPassword(email, password);
        if (userByEmailAndPassword.isEmpty()) {
            throw new AuthenticationException("Invalid email or password.");
        }

        UUID token = UUID.randomUUID();
        UserModel userModel = userByEmailAndPassword.get();
        userModel.setToken(token);
        userRepository.save(userModel);
        return token;
    }

    @Override
    public boolean isUserAuthorized(UUID token) {
        Optional<UserModel> userByEmailAndToken = findByToken(token);
        return userByEmailAndToken.isPresent();
    }

    @Override
    public Optional<UserModel> findByToken(UUID token) {
        return userRepository.findUserByToken(token);
    }
}
