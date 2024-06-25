package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.repositories.BookingRepository;
import com.felipegc.booking.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Optional<BookingModel> findById(UUID bookingId) {
        return bookingRepository.findById(bookingId);
    }
}
