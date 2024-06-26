package com.felipegc.booking.services.impl;

import com.felipegc.booking.enums.BookingStatus;
import com.felipegc.booking.models.BookingModel;
import com.felipegc.booking.repositories.BookingRepository;
import com.felipegc.booking.services.BookingService;
import com.felipegc.booking.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Override
    public BookingModel save(BookingModel bookingModel) {
        validateDates(bookingModel);
        validateRangeDateOverlaps(bookingModel);

        return bookingRepository.save(bookingModel);
    }

    @Override
    public void changeStatus(BookingModel bookingModel, BookingStatus status) {
        validateCancellationTime(bookingModel, status);
        if (bookingModel.getStatus().equals(BookingStatus.CANCELED) && status.equals(BookingStatus.RESERVED))
            validateRangeDateOverlaps(bookingModel);

        bookingModel.setStatus(status);
        bookingRepository.save(bookingModel);
    }

    private static void validateDates(BookingModel bookingModel) {
        if (DateUtils.isStartDateBiggerThanEndDate(bookingModel.getStartDate(), bookingModel.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
    }

    private void validateRangeDateOverlaps(BookingModel bookingModel) {
        List<BookingModel> bookings = bookingRepository.findAllBookingsByPropertyIdAndStatus(
                bookingModel.getProperty().getPropertyId(), BookingStatus.RESERVED.name());
        Optional<BookingModel> first = bookings.stream().filter(
                booking ->
                        !booking.getBookingId().equals(bookingModel.getBookingId()) &&
                                DateUtils.isDateRageOverlap(
                                        bookingModel.getStartDate(), bookingModel.getEndDate(),
                                        booking.getStartDate(), booking.getEndDate())).findFirst();

        if (first.isPresent()) {
            throw new IllegalArgumentException("Date range overlap with another booking.");
        }
    }

    private static void validateCancellationTime(BookingModel bookingModel, BookingStatus status) {
        LocalDate now = DateUtils.getTimeNow();
        if (status.equals(BookingStatus.CANCELED)
                && DateUtils.calculateWeeksDifference(now, bookingModel.getStartDate()) < 1) {

            throw new IllegalArgumentException(
                    "Cancellation is only acceptable with at least one week in advance.");
        }
    }

    @Override
    public Optional<BookingModel> findById(UUID bookingId) {
        return bookingRepository.findById(bookingId);
    }

}
