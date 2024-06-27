package com.felipegc.booking.services.impl;

import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.models.BlockModel;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.repositories.BlockRepository;
import com.felipegc.booking.repositories.PropertyRepository;
import com.felipegc.booking.services.PropertyService;
import com.felipegc.booking.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    BlockRepository blockRepository;

    @Override
    public PropertyModel save(PropertyModel propertyModel) {
        return propertyRepository.save(propertyModel);
    }

    @Override
    public BlockModel addBlock(BlockModel blockModel, UserModel userModel) {
        validateIfUserIsOwner(blockModel, userModel);
        validateDateRangeOverlapsWithBooking(blockModel);
        validateDateRangeOverlapsWithBlock(blockModel);

        return blockRepository.save(blockModel);
    }

    private static void validateIfUserIsOwner(BlockModel blockModel, UserModel userModel) {
        if(!blockModel.getProperty().getOwner().getUserId().equals(userModel.getUserId())) {
            throw new IllegalArgumentException("Only the owner of a property can add a block.");
        }
    }

    private static void validateDateRangeOverlapsWithBooking(BlockModel blockModel) {
        List<BookingModel> bookings = blockModel.getProperty().getBookings().stream().filter(
                booking -> booking.getStatus().equals(BookingStatus.RESERVED)).toList();

        Optional<BookingModel> first = bookings.stream().filter(
                booking -> DateUtils.isDateRangeOverlap(
                                        blockModel.getStartDate(), blockModel.getEndDate(),
                                        booking.getStartDate(), booking.getEndDate())).findFirst();

        if (first.isPresent()) {
            throw new IllegalArgumentException("Date range overlap with a booking.");
        }
    }

    private static void validateDateRangeOverlapsWithBlock(BlockModel blockModel) {
        List<BlockModel> blocks = blockModel.getProperty().getBlocks();

        Optional<BlockModel> first = blocks.stream().filter(
                block -> DateUtils.isDateRangeOverlap(
                        blockModel.getStartDate(), blockModel.getEndDate(),
                        block.getStartDate(), block.getEndDate())).findFirst();

        if (first.isPresent()) {
            throw new IllegalArgumentException("Date range overlap with another block.");
        }
    }

    @Override
    public Optional<PropertyModel> findById(UUID propertyId) {
        return propertyRepository.findById(propertyId);
    }

    @Override
    public List<PropertyModel> getAllProperties() {
        return propertyRepository.findAll();
    }
}
