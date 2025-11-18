package com.exadel.bookService.service;

import com.exadel.bookService.exception.ReservationNotAllowedException;
import com.exadel.bookService.model.Reservation;
import com.exadel.bookService.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service

public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final IBookService bookService;

    public ReservationService(ReservationRepository reservationRepository, IBookService bookService) {
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
    }

    @Transactional
    @Override
    public Reservation reserveBook(Long bookId, Long userId) {
        var book = bookService.getBookById(bookId);

        if (book.getQuantity() > 0) {
            throw new ReservationNotAllowedException("Book is available, no need to reserve");
        }

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUserId(userId);
        reservation.setReservedAt(java.time.LocalDateTime.now());
        reservation.setFulfilled(false);

        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getReservationsByBook(Long bookId) {
        return reservationRepository.findByBookId(bookId);
    }

    @Override
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public void fulfillReservation(Reservation reservation) {
        reservation.setFulfilled(true);
        reservationRepository.save(reservation);
    }
}
