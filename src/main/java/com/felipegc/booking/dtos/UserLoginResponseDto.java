package com.felipegc.booking.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UserLoginResponseDto {
    private UUID token;
}
