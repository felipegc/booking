package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.BookingDto;
import com.felipegc.booking.exceptions.GeneralException;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.services.BookingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    BookingService bookingService;

    // TODO(felipegc): may we add a validation here?
    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestBody BookingDto bookingDto) {
        BookingModel bookingModel = new BookingModel();
        BeanUtils.copyProperties(bookingDto, bookingModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.save(bookingModel));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable(value = "bookingId") UUID bookingId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingById(bookingId));
        } catch (GeneralException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getDetail());
        }
    }
}
