package com.felipegc.booking.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GeneralException extends RuntimeException {

    private final HttpStatus status;
    private final String detail;

    public GeneralException(HttpStatus status, String detail) {
        super(detail);
        this.status = status;
        this.detail = detail;
    }
}
