package com.exadel.bookService.exception;

public class ReservationNotAllowedException extends IllegalStateException {
    public ReservationNotAllowedException(String message) {
        super(message);
    }

    }