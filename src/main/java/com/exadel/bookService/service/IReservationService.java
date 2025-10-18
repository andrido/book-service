package com.exadel.bookService.service;

import com.exadel.bookService.model.Reservation;

import java.util.List;

public interface IReservationService {
    Reservation reserveBook(Long bookId, Long userId);
    List<Reservation> getReservationsByUser(Long userId);
    List<Reservation> getReservationsByBook(Long bookId);
    void fulfillReservation(Reservation reservation);
}
