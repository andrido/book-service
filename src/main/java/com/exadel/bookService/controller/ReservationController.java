package com.exadel.bookService.controller;

import com.exadel.bookService.dto.ReservationRequest;
import com.exadel.bookService.model.Reservation;
import com.exadel.bookService.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Reservation reserveBook(@RequestBody ReservationRequest request) {
        return reservationService.reserveBook(request.getBookId(), request.getUserId());
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getReservationsByUser(@PathVariable Long userId) {
        return reservationService.getReservationsByUser(userId);
    }

    @GetMapping("/book/{bookId}")
    public List<Reservation> getReservationsByBook(@PathVariable Long bookId) {
        return reservationService.getReservationsByBook(bookId);
    }
}
