package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.repositories.BookingRepository;
import com.felipegc.booking.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
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
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
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

    @Test
    void When_Save_WithDateRangeAndGuestDetailsUpdate_ShouldSucceed() {
        UUID bookingModelId = UUID.randomUUID();

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(bookingModelId);
        bookingModel.setGuestDetails("Guest Details");
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
        bookingModel2.setGuestDetails("Guest Details2");
        bookingModel2.setStartDate(LocalDate.parse("2024-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2024-01-15"));

        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);

        when(bookingRepository.findAllBookingsByPropertyIdAndStatus(propertyUUID, BookingStatus.RESERVED.name()))
                .thenReturn(List.of(bookingModel, bookingModel2));

        BookingModel toSave = new BookingModel();
        toSave.setBookingId(bookingModelId);
        toSave.setGuestDetails("Update Guest Details");
        toSave.setProperty(propertyModel);
        toSave.setStartDate(LocalDate.parse("2024-02-07"));
        toSave.setEndDate(LocalDate.parse("2024-02-11"));

        bookingService.save(toSave);

        verify(bookingRepository).save(toSave);
    }

    @Test
    void When_ChangeStatus_WithCancellationTimeLessThanOneWeek_ShouldThrowIllegalArgumentException() {
        try (MockedStatic<DateUtils> utilities = mockStatic(DateUtils.class)) {
            utilities.when(DateUtils::getTimeNow).thenReturn(LocalDate.parse("2023-12-30"));

            BookingModel bookingModel = new BookingModel();
            bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
            bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
            bookingModel.setStatus(BookingStatus.RESERVED);

            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> bookingService.changeStatus(
                            bookingModel, BookingStatus.CANCELED));
            assertEquals("Cancellation is only acceptable with at least one week in advance.",
                    ex.getMessage());
        }
    }

    @Test
    void When_ChangeStatus_WithCancellationTimeGreaterThanOneWeek_ShouldSucceed() {
        BookingModel bookingModel = new BookingModel();
        bookingModel.setStartDate(LocalDate.parse("2099-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2099-01-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);

        bookingService.changeStatus(bookingModel, BookingStatus.CANCELED);

        verify(bookingRepository).save(bookingModel);
    }

    @Test
    void When_ChangeStatus_WithCancellationTimeGreaterThanOneWeekAndStatusReserved_ShouldSucceed() {
        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);

        UUID bookingModelId = UUID.randomUUID();
        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(bookingModelId);
        bookingModel.setGuestDetails("Guest Details");
        bookingModel.setStartDate(LocalDate.parse("2099-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2099-01-10"));
        bookingModel.setStatus(BookingStatus.CANCELED);
        bookingModel.setProperty(propertyModel);

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
        bookingModel2.setGuestDetails("Guest Details");
        bookingModel2.setStartDate(LocalDate.parse("2099-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2099-01-15"));
        bookingModel2.setProperty(propertyModel);

        when(bookingRepository.findAllBookingsByPropertyIdAndStatus(propertyUUID, BookingStatus.RESERVED.name()))
                .thenReturn(List.of(bookingModel2)); // CANCELED status wont return

        bookingService.changeStatus(bookingModel, BookingStatus.RESERVED);

        verify(bookingRepository).save(bookingModel);
    }

    @Test
    void When_ChangeStatus_WithStatusReservedAndDateRangeOverlap_ShouldThrowIllegalArgumentException() {
        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);

        UUID bookingModelId = UUID.randomUUID();

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(bookingModelId);
        bookingModel.setGuestDetails("Guest Details");
        bookingModel.setStartDate(LocalDate.parse("2099-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2099-01-10"));
        bookingModel.setStatus(BookingStatus.CANCELED);
        bookingModel.setProperty(propertyModel);

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
        bookingModel2.setGuestDetails("Guest Details2");
        bookingModel2.setStartDate(LocalDate.parse("2099-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2099-01-15"));
        bookingModel2.setStatus(BookingStatus.RESERVED);
        bookingModel2.setProperty(propertyModel);

        BookingModel bookingModel3 = new BookingModel();
        bookingModel3.setBookingId(UUID.randomUUID());
        bookingModel3.setGuestDetails("Guest Details3");
        bookingModel3.setStartDate(LocalDate.parse("2099-01-03")); // date will overlap with bookingModel
        bookingModel3.setEndDate(LocalDate.parse("2099-01-08"));
        bookingModel3.setStatus(BookingStatus.RESERVED);
        bookingModel3.setProperty(propertyModel);

        when(bookingRepository.findAllBookingsByPropertyIdAndStatus(propertyUUID, BookingStatus.RESERVED.name()))
                .thenReturn(List.of(bookingModel2, bookingModel3));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        bookingService.changeStatus(bookingModel, BookingStatus.RESERVED));
        assertEquals("Date range overlap with another booking.", ex.getMessage());
    }

}