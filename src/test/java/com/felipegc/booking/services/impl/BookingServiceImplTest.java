package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.enums.BookingStatus;
import com.felipegc.booking.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void When_Save_WithStartDateAfterEndDate_ShouldThrowIllegalArgumentException() {
        BookingModel bookingModel = new BookingModel();
        bookingModel.setStartDate(LocalDate.parse("2024-01-10"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-01"));
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> bookingService.save(bookingModel));
        assertEquals("Start date must be before end date.", ex.getMessage());
    }

    @Test
    void When_Save_WithDateRangeOverlap_ShouldThrowIllegalArgumentException() {
        BookingModel bookingModel = new BookingModel();
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setStartDate(LocalDate.parse("2024-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2024-01-15"));

        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);

        when(bookingRepository.findAllBookingsByPropertyIdAndStatus(propertyUUID, BookingStatus.RESERVED.name()))
                .thenReturn(List.of(bookingModel, bookingModel2));

        BookingModel toSave = new BookingModel();
        toSave.setProperty(propertyModel);
        toSave.setStartDate(LocalDate.parse("2024-01-07"));
        toSave.setEndDate(LocalDate.parse("2024-01-11"));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> bookingService.save(toSave));
        assertEquals("Date range overlap with another booking.", ex.getMessage());
    }
}