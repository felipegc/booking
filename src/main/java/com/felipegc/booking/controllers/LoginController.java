package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.UserLoginDto;
import com.felipegc.booking.dtos.UserLoginResponseDto;
import com.felipegc.booking.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Login.",
            description = "Login. The response is the token.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "The login is successful.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "The login is unauthorized.", content = @Content),
            }
    )
    @PostMapping
    public ResponseEntity<Object> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        try {
            UUID token = userService.login(userLoginDto.getEmail(), userLoginDto.getPassword());
            UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto();
            userLoginResponseDto.setToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(userLoginResponseDto);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
