package com.felipegc.booking.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateBookingDto {
    @NotBlank
    private String guestDetails;

    @NotNull
    @FutureOrPresent
    @Schema(description = "Future or Present date.")
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent
    @Schema(description = "Future or Present date.")
    private LocalDate endDate;
}
