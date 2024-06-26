package com.felipegc.booking.models;

import com.felipegc.booking.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TB_BOOKING")
public class BookingModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bookingId;

    @Column(nullable = false, length = 250)
    private String guestDetails;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "property_id", referencedColumnName = "propertyId")
    private PropertyModel property;

    @ManyToOne
    @JoinColumn(name = "guest_id", referencedColumnName = "userId")
    private UserModel guest;
}
