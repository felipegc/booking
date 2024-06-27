package com.felipegc.booking.services.impl;

import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.models.BlockModel;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.repositories.BlockRepository;
import com.felipegc.booking.repositories.BookingRepository;
import com.felipegc.booking.repositories.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Test
    void When_AddBlock_WithUserDifferentFromOwner_ShouldThrowIllegalArgumentException() {
        UserModel userModel = new UserModel();
        userModel.setUserId(UUID.randomUUID());

        UserModel owner = new UserModel();
        owner.setUserId(UUID.randomUUID());

        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setOwner(owner);

        BlockModel blockModel = new BlockModel();
        blockModel.setStartDate(LocalDate.parse("2024-01-11"));
        blockModel.setEndDate(LocalDate.parse("2024-01-15"));
        blockModel.setProperty(propertyModel);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> propertyService.addBlock(blockModel, userModel));
        assertEquals("Only the owner of a property can add a block.", ex.getMessage());
    }

    @Test
    void When_AddBlock_WithBlockAndBookingOverlap_ShouldThrowIllegalArgumentException() {
        UserModel owner = new UserModel();
        owner.setUserId(UUID.randomUUID());

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);

        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setOwner(owner);
        propertyModel.setBookings(List.of(bookingModel));
        bookingModel.setProperty(propertyModel);

        BlockModel blockModel = new BlockModel();
        blockModel.setStartDate(LocalDate.parse("2024-01-04"));
        blockModel.setEndDate(LocalDate.parse("2024-01-15"));
        blockModel.setProperty(propertyModel);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> propertyService.addBlock(blockModel, owner));
        assertEquals("Date range overlap with a booking.", ex.getMessage());
    }

    @Test
    void When_AddBlock_WithBlockAndCancelledBookingOverlap_ShouldSucceed() {
        UserModel owner = new UserModel();
        owner.setUserId(UUID.randomUUID());

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-01-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-10"));
        bookingModel.setStatus(BookingStatus.CANCELED);

        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setOwner(owner);
        propertyModel.setBookings(List.of(bookingModel));
        bookingModel.setProperty(propertyModel);
        propertyModel.setBlocks(List.of());

        BlockModel blockModel = new BlockModel();
        blockModel.setStartDate(LocalDate.parse("2024-01-04"));
        blockModel.setEndDate(LocalDate.parse("2024-01-15"));
        blockModel.setProperty(propertyModel);

        propertyService.addBlock(blockModel, owner);

        verify(blockRepository).save(blockModel);
    }

    @Test
    void When_AddBlock_WithBlockAndBlockOverlap_ShouldThrowIllegalArgumentException() {
        UserModel owner = new UserModel();
        owner.setUserId(UUID.randomUUID());

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-10-01"));
        bookingModel.setEndDate(LocalDate.parse("2024-10-10"));
        bookingModel.setStatus(BookingStatus.RESERVED);

        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setOwner(owner);
        propertyModel.setBookings(List.of(bookingModel));
        bookingModel.setProperty(propertyModel);

        BlockModel existingBlock = new BlockModel();
        existingBlock.setStartDate(LocalDate.parse("2024-01-01"));
        existingBlock.setEndDate(LocalDate.parse("2024-01-15"));
        existingBlock.setProperty(propertyModel);
        propertyModel.setBlocks(List.of(existingBlock));

        BlockModel blockModel = new BlockModel();
        blockModel.setStartDate(LocalDate.parse("2024-01-03"));
        blockModel.setEndDate(LocalDate.parse("2024-01-07"));
        blockModel.setProperty(propertyModel);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> propertyService.addBlock(blockModel, owner));
        assertEquals("Date range overlap with another block.", ex.getMessage());
    }

}