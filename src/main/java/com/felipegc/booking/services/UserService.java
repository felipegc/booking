package com.felipegc.booking.services;

import com.felipegc.booking.models.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserModel save(UserModel userModel);

    Optional<UserModel> findById(UUID userId);

    boolean isUserAuthorized(String userId, String token);
}
