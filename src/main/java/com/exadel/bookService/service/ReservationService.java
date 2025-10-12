package com.exadel.bookService.service;

import com.exadel.bookService.*;
import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.model.Reservation;
import com.exadel.bookService.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookService bookService;

    public ReservationService(ReservationRepository reservationRepository, BookService bookService) {
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
    }

    @Transactional
    public Reservation reserveBook(Long bookId, Long userId) {
        Book book = bookService.getBookById(bookId);


        // Se livro disponível, talvez não precise reservar, só emprestar
        if (book.getQuantity() > 0) {
            throw new IllegalStateException("Book is available, no need to reserve");
        }

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUserId(userId);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setFulfilled(false);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByBook(Long bookId) {
        return reservationRepository.findByBookId(bookId);
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Transactional
    public void fulfillReservation(Reservation reservation) {
        reservation.setFulfilled(true);
        reservationRepository.save(reservation);
    }
}
