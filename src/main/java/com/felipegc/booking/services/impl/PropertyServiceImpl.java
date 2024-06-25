package com.felipegc.booking.services.impl;

import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.repositories.PropertyRepository;
import com.felipegc.booking.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    PropertyRepository propertyRepository;

    @Override
    public PropertyModel save(PropertyModel propertyModel) {
        return propertyRepository.save(propertyModel);
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
