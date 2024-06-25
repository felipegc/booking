package com.felipegc.booking.services;

import com.felipegc.booking.models.PropertyModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyService {

    PropertyModel save(PropertyModel propertyModel);

    Optional<PropertyModel> findById(UUID propertyId);

    List<PropertyModel> getAllProperties();
}
