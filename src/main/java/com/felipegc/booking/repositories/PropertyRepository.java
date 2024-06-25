package com.felipegc.booking.repositories;

import com.felipegc.booking.models.PropertyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<PropertyModel, UUID>, JpaSpecificationExecutor<PropertyModel> {
}
