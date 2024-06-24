package com.felipegc.booking.services;

import com.felipegc.booking.models.BookingModel;

import java.util.UUID;

public interface BookingService {

    BookingModel save(BookingModel bookingModel);

    BookingModel getBookingById(UUID bookingId);
}
