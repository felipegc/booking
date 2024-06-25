package com.felipegc.booking.services.impl;

import com.felipegc.booking.exceptions.GeneralException;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.repositories.BookingRepository;
import com.felipegc.booking.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Override
    public BookingModel save(BookingModel bookingModel) {
        return bookingRepository.save(bookingModel);
    }

    @Override
    public BookingModel getBookingById(UUID bookingId) {
        Optional<BookingModel> byId = bookingRepository.findById(bookingId);
        if (byId.isEmpty()) {
            throw new GeneralException(HttpStatus.NOT_FOUND, "Booking not found");
        }
        return byId.get();
    }
}
