package com.felipegc.booking.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingDto {

    @NotBlank
    private String description;
}
