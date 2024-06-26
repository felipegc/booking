package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.repositories.UserRepository;
import com.felipegc.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public boolean isUserAuthorized(String userId, String token) {
        return true;
    }
}
