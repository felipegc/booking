package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.BookingDto;
import com.felipegc.booking.dtos.UpdateBookingDto;
import com.felipegc.booking.dtos.UpdateBookingStatusDto;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.services.BookingService;
import com.felipegc.booking.services.PropertyService;
import com.felipegc.booking.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    PropertyService propertyService;

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestBody @Valid BookingDto bookingDto) {
        Optional<PropertyModel> propertyModel = propertyService.findById(bookingDto.getPropertyId());
        if (propertyModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property Not Found.");
        }

        Optional<UserModel> userModel = userService.findById(bookingDto.getGuestId());
        if (userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        BookingModel bookingModel = new BookingModel();
        BeanUtils.copyProperties(bookingDto, bookingModel);
        bookingModel.setProperty(propertyModel.get());
        bookingModel.setGuest(userModel.get());
        bookingModel.setStatus(BookingStatus.RESERVED);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.save(bookingModel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable(value = "bookingId") UUID bookingId) {
        Optional<BookingModel> bookingModel = bookingService.findById(bookingId);
        if (bookingModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(bookingService.findById(bookingId));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable(value = "bookingId") UUID bookingId,
                                                @RequestBody @Valid UpdateBookingDto updateBookingDto) {
        Optional<BookingModel> bookingModelOptional = bookingService.findById(bookingId);
        if (bookingModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking Not Found.");
        }

        BookingModel bookingModel = bookingModelOptional.get();
        bookingModel.setStartDate(updateBookingDto.getStartDate());
        bookingModel.setEndDate(updateBookingDto.getEndDate());
        bookingModel.setGuestDetails(updateBookingDto.getGuestDetails());

        try {
            return ResponseEntity.status(HttpStatus.OK).body(bookingService.save(bookingModel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@PathVariable(value = "bookingId") UUID bookingId,
                                                      @RequestBody @Valid UpdateBookingStatusDto updateBookingDto) {
        Optional<BookingModel> bookingModelOptional = bookingService.findById(bookingId);
        if (bookingModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking Not Found.");
        }

        BookingModel bookingModel = bookingModelOptional.get();

        try {
            bookingService.changeStatus(bookingModel, updateBookingDto.getStatus());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
