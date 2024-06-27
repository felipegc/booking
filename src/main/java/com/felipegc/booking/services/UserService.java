package com.felipegc.booking.services;

import com.felipegc.booking.models.UserModel;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserModel save(UserModel userModel);

    Optional<UserModel> findById(UUID userId);

    UUID login(String email, String password) throws AuthenticationException;

    boolean isUserAuthorized(UUID token);

    Optional<UserModel> findByToken(UUID token);
}
