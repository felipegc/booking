package com.felipegc.booking.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotNull
    private String name;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 4, max = 30)
    private String password;
}
