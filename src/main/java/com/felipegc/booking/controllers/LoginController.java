package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.UserLoginDto;
import com.felipegc.booking.dtos.UserLoginResponseDto;
import com.felipegc.booking.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoginController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Object> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        try {
            UUID login = userService.login(userLoginDto.getEmail(), userLoginDto.getPassword());
            UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto();
            userLoginResponseDto.setToken(login);
            return ResponseEntity.status(HttpStatus.OK).body(login);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
