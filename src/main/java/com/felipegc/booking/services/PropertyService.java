package com.felipegc.booking.services;

import com.felipegc.booking.models.BlockModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyService {

    PropertyModel save(PropertyModel propertyModel);

    BlockModel addBlock(BlockModel blockModel, UserModel userModel);

    Optional<PropertyModel> findById(UUID propertyId);

    List<PropertyModel> getAllProperties();
}
