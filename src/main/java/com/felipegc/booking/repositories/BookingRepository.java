package com.felipegc.booking.repositories;

import com.felipegc.booking.models.BookingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingModel, UUID>, JpaSpecificationExecutor<BookingModel> {

    @Query(value="select * from tb_booking where property_id = :propertyId and status = :status", nativeQuery = true)
    List<BookingModel> findAllBookingsByPropertyIdAndStatus(
            @Param("propertyId") UUID propertyId, @Param("status") String status);
}

