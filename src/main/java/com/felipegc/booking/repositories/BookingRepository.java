package com.felipegc.booking.repositories;

import com.felipegc.booking.models.BookingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingModel, UUID>, JpaSpecificationExecutor<BookingModel> {}

