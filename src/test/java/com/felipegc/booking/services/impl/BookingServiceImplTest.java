package com.felipegc.booking.services.impl;

import com.felipegc.booking.exceptions.GeneralException;
import com.felipegc.booking.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

// TODO(felipegc): remove it
//    @Test
//    void When_GetBookingById_WithNonExistingId_ShouldThrowNotFoundException() {
//        when(bookingRepository.findById(any())).thenReturn(Optional.empty());
//
//        GeneralException generalException =
//                assertThrows(GeneralException.class, () -> bookingService.getBookingById(any()));
//        assertEquals(HttpStatus.NOT_FOUND, generalException.getStatus());
//    }
}