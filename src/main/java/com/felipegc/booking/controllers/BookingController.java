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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @Operation(summary = "Save a new booking.",
            description = "Save a new booking. The response is the booking saved containing its detailed information.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201",description = "The booking is created.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "The property or user is not found", content = @Content),
            })
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

    @Operation(summary = "Get a booking by its ID.",
            description = "Get a booking by its ID. The response is the booking saved containing its detailed information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The booking is found.", content = @Content),
            @ApiResponse(responseCode = "404", description = "The booking is not found.", content = @Content),
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable(value = "bookingId") UUID bookingId) {
        Optional<BookingModel> bookingModel = bookingService.findById(bookingId);
        if (bookingModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(bookingService.findById(bookingId));
    }

    @Operation(summary = "Update a booking by its ID.",
            description = "Update a booking by its ID. The response is the booking saved containing its detailed information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The booking is updated.", content = @Content),
            @ApiResponse(responseCode = "400", description = "The booking is not updated.", content = @Content),
            @ApiResponse(responseCode = "404", description = "The booking is not found.", content = @Content),
    })
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

    @Operation(summary = "Change the status of a booking by its ID.",
            description = "Change the status of a booking by its ID. The response is the booking saved containing its detailed information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The booking status is changed.", content = @Content),
            @ApiResponse(responseCode = "400", description = "The booking status is not changed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "The booking is not found.", content = @Content),
    })
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

    @Operation(summary = "Delete a booking by its ID.",
            description = "Delete a booking by its ID. The response is the status of the operation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The booking is deleted.", content = @Content),
            @ApiResponse(responseCode = "400", description = "The booking is not deleted.", content = @Content),
            @ApiResponse(responseCode = "404", description = "The booking is not found.", content = @Content),
    })
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> deleteBooking(@PathVariable(value = "bookingId") UUID bookingId) {
        Optional<BookingModel> bookingModelOptional = bookingService.findById(bookingId);
        if (bookingModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking Not Found.");
        }

        try {
            bookingService.delete(bookingModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
