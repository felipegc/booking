package com.felipegc.booking.utils;

import java.time.LocalDate;

public class DateUtils {

    public static boolean isStartDateBiggerThanEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }

    // reference: https://www.baeldung.com/java-check-two-date-ranges-overlap
    public static boolean isDateRageOverlap(
            LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        return !(endDate1.isBefore(startDate2) || startDate1.isAfter(endDate2));
    }
}
