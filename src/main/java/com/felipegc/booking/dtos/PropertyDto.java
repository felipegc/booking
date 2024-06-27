package com.felipegc.booking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PropertyDto {

    @NotBlank
    private String description;
}
