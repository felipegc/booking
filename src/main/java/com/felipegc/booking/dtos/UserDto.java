package com.felipegc.booking.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {

    @NotNull
    private String name;
}
