package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.UserDto;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Save a new user.",
            description = "Save a new user. The response is the user saved containing its detailed information.")
    @ApiResponses(
            value = {
            @ApiResponse(responseCode = "201", description = "The user is created."),
            @ApiResponse(responseCode = "409", description = "The user already exists.")
    })
    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto userDto) {
        try {
            UserModel userModel = new UserModel();
            BeanUtils.copyProperties(userDto, userModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
