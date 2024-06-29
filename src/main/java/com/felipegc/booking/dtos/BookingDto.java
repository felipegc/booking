package com.felipegc.booking.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BookingDto {

    @NotBlank
    private String guestDetails;

    @NotNull
    private UUID propertyId;

    @NotNull
    private UUID guestId;

    @NotNull
    @FutureOrPresent
    @Schema(description = "Future or Present date.")
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent
    @Schema(description = "Future or Present date.")
    private LocalDate endDate;
}
