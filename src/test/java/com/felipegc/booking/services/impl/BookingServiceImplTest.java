package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.BlockModel;
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
    void When_Save_WithDateRangeOverlapWithAnotherBooking_ShouldThrowIllegalArgumentException() {
        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);
        bookingModel.setProperty(propertyModel);

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
        bookingModel2.setStartDate(LocalDate.parse("2024-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2024-01-15"));
        bookingModel2.setStatus(BookingStatus.RESERVED);
        bookingModel2.setProperty(propertyModel);

        propertyModel.setBookings(List.of(bookingModel, bookingModel2));

        BookingModel toSave = new BookingModel();
        toSave.setProperty(propertyModel);
        toSave.setStartDate(LocalDate.parse("2024-01-07"));
        toSave.setEndDate(LocalDate.parse("2024-01-11"));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> bookingService.save(toSave));
        assertEquals("Date range overlap with another booking.", ex.getMessage());
    }

    @Test
    void When_Save_WithDateRangeOverlapWithBlock_ShouldThrowIllegalArgumentException() {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlockId(UUID.randomUUID());
        blockModel.setStartDate(LocalDate.parse("2024-02-07"));
        blockModel.setEndDate(LocalDate.parse("2024-02-15"));

        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);
        propertyModel.setBlocks(List.of(blockModel));
        blockModel.setProperty(propertyModel);

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);
        bookingModel.setProperty(propertyModel);

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
        bookingModel2.setStartDate(LocalDate.parse("2024-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2024-01-15"));
        bookingModel2.setStatus(BookingStatus.RESERVED);
        bookingModel2.setProperty(propertyModel);

        propertyModel.setBookings(List.of(bookingModel, bookingModel2));

        BookingModel toSave = new BookingModel();
        toSave.setProperty(propertyModel);
        toSave.setStartDate(LocalDate.parse("2024-02-07"));
        toSave.setEndDate(LocalDate.parse("2024-02-11"));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> bookingService.save(toSave));
        assertEquals("Date range overlap with a block.", ex.getMessage());
    }

    @Test
    void When_Save_WithDateRangeAndGuestDetailsUpdate_ShouldSucceed() {
        UUID bookingModelId = UUID.randomUUID();

        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);
        propertyModel.setBlocks(List.of());

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(bookingModelId);
        bookingModel.setGuestDetails("Guest Details");
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);
        bookingModel.setProperty(propertyModel);

        BookingModel bookingModel2 = new BookingModel();
        bookingModel2.setBookingId(UUID.randomUUID());
        bookingModel2.setGuestDetails("Guest Details2");
        bookingModel2.setStartDate(LocalDate.parse("2024-01-11"));
        bookingModel2.setEndDate(LocalDate.parse("2024-01-15"));
        bookingModel2.setStatus(BookingStatus.RESERVED);
        bookingModel2.setProperty(propertyModel);

        propertyModel.setBookings(List.of(bookingModel, bookingModel2));

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
        propertyModel.setBlocks(List.of());

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
        bookingModel2.setStatus(BookingStatus.RESERVED);
        bookingModel2.setProperty(propertyModel);

        propertyModel.setBookings(List.of(bookingModel, bookingModel2));

        bookingService.changeStatus(bookingModel, BookingStatus.RESERVED);

        verify(bookingRepository).save(bookingModel);
    }

    @Test
    void When_ChangeStatus_WithStatusReservedAndDateRangeOverlapsWithAnotherBooking_ShouldThrowIllegalArgumentException() {
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

        propertyModel.setBookings(List.of(bookingModel, bookingModel2, bookingModel3));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        bookingService.changeStatus(bookingModel, BookingStatus.RESERVED));
        assertEquals("Date range overlap with another booking.", ex.getMessage());
    }

    @Test
    void When_ChangeStatus_WithStatusReservedAndDateRangeOverlapsWithBlock_ShouldThrowIllegalArgumentException() {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlockId(UUID.randomUUID());
        blockModel.setStartDate(LocalDate.parse("2099-01-07"));
        blockModel.setEndDate(LocalDate.parse("2099-01-15"));

        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);
        propertyModel.setBlocks(List.of(blockModel));

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
        bookingModel3.setStartDate(LocalDate.parse("2099-04-03"));
        bookingModel3.setEndDate(LocalDate.parse("2099-04-08"));
        bookingModel3.setStatus(BookingStatus.RESERVED);
        bookingModel3.setProperty(propertyModel);

        propertyModel.setBookings(List.of(bookingModel, bookingModel2, bookingModel3));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        bookingService.changeStatus(bookingModel, BookingStatus.RESERVED));
        assertEquals("Date range overlap with a block.", ex.getMessage());
    }

    @Test
    void When_Delete_WithDeletionTimeLessThanOneWeek_ShouldThrowIllegalArgumentException() {
        try (MockedStatic<DateUtils> utilities = mockStatic(DateUtils.class)) {
            utilities.when(DateUtils::getTimeNow).thenReturn(LocalDate.parse("2023-12-30"));

            BookingModel bookingModel = new BookingModel();
            bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
            bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
            bookingModel.setStatus(BookingStatus.RESERVED);

            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> bookingService.delete(bookingModel));
            assertEquals("Deletion is only acceptable with at least one week in advance.",
                    ex.getMessage());
        }
    }

    @Test
    void When_Delete_WithDeletionTimeGreaterThanOneWeek_ShouldSucceed() {
        BookingModel bookingModel = new BookingModel();
        bookingModel.setStartDate(LocalDate.parse("2099-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2099-01-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);

        bookingService.delete(bookingModel);

        verify(bookingRepository).delete(bookingModel);
    }
}