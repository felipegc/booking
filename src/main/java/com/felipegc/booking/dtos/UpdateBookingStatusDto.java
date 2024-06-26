package com.felipegc.booking.dtos;

import com.felipegc.booking.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBookingStatusDto {

    @NotNull
    private BookingStatus status;
}
