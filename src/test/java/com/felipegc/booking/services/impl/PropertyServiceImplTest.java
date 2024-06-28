package com.felipegc.booking.services.impl;

import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.models.BlockModel;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.repositories.BlockRepository;
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
    void When_SaveBlock_WithUserDifferentFromOwner_ShouldThrowIllegalArgumentException() {
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
                assertThrows(IllegalArgumentException.class, () -> propertyService.saveBlock(blockModel, userModel));
        assertEquals("Only the owner of a property can add or delete a block.", ex.getMessage());
    }

    @Test
    void When_SaveBlock_WithBlockAndBookingOverlap_ShouldThrowIllegalArgumentException() {
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
                assertThrows(IllegalArgumentException.class, () -> propertyService.saveBlock(blockModel, owner));
        assertEquals("Date range overlap with a booking.", ex.getMessage());
    }

    @Test
    void When_SaveBlock_WithBlockAndCancelledBookingOverlap_ShouldSucceed() {
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

        propertyService.saveBlock(blockModel, owner);

        verify(blockRepository).save(blockModel);
    }

    @Test
    void When_SaveBlock_WithBlockAndBlockOverlap_ShouldThrowIllegalArgumentException() {
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
                assertThrows(IllegalArgumentException.class, () -> propertyService.saveBlock(blockModel, owner));
        assertEquals("Date range overlap with another block.", ex.getMessage());
    }

    @Test
    void When_SaveBlock_WithBlockWithoutOverlapsWithBookingAndBlocks_ShouldSucceed() {
        UUID blockModelId = UUID.randomUUID();

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
        blockModel.setBlockId(blockModelId);
        blockModel.setStartDate(LocalDate.parse("2024-01-04"));
        blockModel.setEndDate(LocalDate.parse("2024-01-15"));
        blockModel.setProperty(propertyModel);

        BlockModel blockModel2 = new BlockModel();
        blockModel2.setStartDate(LocalDate.parse("2024-02-04"));
        blockModel2.setEndDate(LocalDate.parse("2024-02-15"));
        blockModel2.setProperty(propertyModel);

        propertyModel.setBlocks(List.of(blockModel, blockModel2));

        BlockModel toSave = new BlockModel();
        toSave.setBlockId(blockModelId);
        toSave.setStartDate(LocalDate.parse("2024-03-27"));
        toSave.setEndDate(LocalDate.parse("2024-03-29"));
        toSave.setReason("updateReason");
        toSave.setProperty(propertyModel);

        propertyService.saveBlock(toSave, owner);

        verify(blockRepository).save(toSave);
    }

    @Test
    void When_SaveBlock_WithWithoutDateRangeUpdate_ShouldSucceed() {
        UUID blockModelId = UUID.randomUUID();

        UserModel owner = new UserModel();
        owner.setUserId(UUID.randomUUID());

        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingId(UUID.randomUUID());
        bookingModel.setStartDate(LocalDate.parse("2024-01-22"));
        bookingModel.setEndDate(LocalDate.parse("2024-01-24"));
        bookingModel.setStatus(BookingStatus.RESERVED);

        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setOwner(owner);
        propertyModel.setBookings(List.of(bookingModel));
        bookingModel.setProperty(propertyModel);

        BlockModel blockModel = new BlockModel();
        blockModel.setBlockId(blockModelId);
        blockModel.setStartDate(LocalDate.parse("2024-01-04"));
        blockModel.setEndDate(LocalDate.parse("2024-01-15"));
        blockModel.setProperty(propertyModel);

        propertyModel.setBlocks(List.of(blockModel));

        BlockModel toSave = new BlockModel();
        toSave.setBlockId(blockModelId);
        toSave.setStartDate(LocalDate.parse("2024-01-04"));
        toSave.setEndDate(LocalDate.parse("2024-01-15"));
        toSave.setReason("updateReason");
        toSave.setProperty(propertyModel);

        propertyService.saveBlock(toSave, owner);

        verify(blockRepository).save(toSave);
    }

    @Test
    void When_DeleteBlock_WithUserDifferentFromOwner_ShouldThrowIllegalArgumentException() {
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
                assertThrows(IllegalArgumentException.class, () -> propertyService.deleteBlock(blockModel, userModel));
        assertEquals("Only the owner of a property can add or delete a block.", ex.getMessage());
    }

    @Test
    void When_DeleteBlock_WithBlock_ShouldSucceed() {
        UserModel owner = new UserModel();
        owner.setUserId(UUID.randomUUID());

        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(UUID.randomUUID());
        propertyModel.setOwner(owner);

        BlockModel blockModel = new BlockModel();
        blockModel.setBlockId(UUID.randomUUID());
        blockModel.setStartDate(LocalDate.parse("2024-01-11"));
        blockModel.setEndDate(LocalDate.parse("2024-01-15"));
        blockModel.setProperty(propertyModel);

        propertyModel.setBlocks(List.of(blockModel));

        propertyService.deleteBlock(blockModel, owner);

        verify(blockRepository).deleteBlockByPropertyIdAndBlockId(
                blockModel.getProperty().getPropertyId(), blockModel.getBlockId());
    }
}