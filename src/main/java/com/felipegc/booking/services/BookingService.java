package com.felipegc.booking.services;

import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.models.BookingModel;

import java.util.Optional;
import java.util.UUID;

public interface BookingService {

    BookingModel save(BookingModel bookingModel);

    Optional<BookingModel> findById(UUID bookingId);

    void changeStatus(BookingModel bookingModel, BookingStatus status);

    void delete(BookingModel bookingModel);
}
