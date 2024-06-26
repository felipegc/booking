package com.felipegc.booking.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void When_StartDateIsAfterEndDate_Then_ReturnTrue() {
        assertTrue(DateUtils.isStartDateBiggerThanEndDate(
                LocalDate.parse("2024-01-10"), LocalDate.parse("2024-01-01")));
    }

    @Test
    void When_DateRangeOverlap_Then_ReturnTrue() {
        assertTrue(DateUtils.isDateRageOverlap(
                LocalDate.parse("2024-01-05"), LocalDate.parse("2024-01-10"),
                LocalDate.parse("2024-01-02"), LocalDate.parse("2024-01-07")));
    }

    @Test
    void When_DateRangeNotOverlap_Then_ReturnFalse() {
        assertFalse(DateUtils.isDateRageOverlap(
                LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-10"),
                LocalDate.parse("2024-01-11"), LocalDate.parse("2024-01-15")));
    }

    @Test
    void When_CalculateWeeksDifference_IsMoreThanZero() {
        assertEquals(2, DateUtils.calculateWeeksDifference(
                LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-15")));
    }

    @Test
    void When_CalculateWeeksDifference_IsLessThanOne() {
        assertEquals(0, DateUtils.calculateWeeksDifference(
                LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-06")));
    }
}