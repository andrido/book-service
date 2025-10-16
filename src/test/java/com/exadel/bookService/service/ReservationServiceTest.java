package com.exadel.bookService.service;

import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.ReservationNotAllowedException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.model.Reservation;
import com.exadel.bookService.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("reserveBook: should create reservation when book unavailable")
    void reserveBook_createsReservation() {
        Book book = new Book();
        book.setId(1L);
        book.setQuantity(0);

        when(bookService.getBookById(1L)).thenReturn(book);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation result = reservationService.reserveBook(1L, 2L);

        assertNotNull(result);
        assertFalse(result.isFulfilled());
        assertEquals(book, result.getBook());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("reserveBook: should throw exception if book available")
    void reserveBook_bookAvailable_throwsException() {
        Book book = new Book();
        book.setId(1L);
        book.setQuantity(3);

        when(bookService.getBookById(1L)).thenReturn(book);

        assertThrows(ReservationNotAllowedException.class, () -> reservationService.reserveBook(1L, 2L));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("getReservationsByBook: should return list")
    void getReservationsByBook_returnsList() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findByBookId(1L)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getReservationsByBook(1L);

        assertEquals(1, result.size());
        verify(reservationRepository).findByBookId(1L);
    }

    @Test
    @DisplayName("getReservationsByUser: should return list")
    void getReservationsByUser_returnsList() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findByUserId(2L)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getReservationsByUser(2L);

        assertEquals(1, result.size());
        verify(reservationRepository).findByUserId(2L);
    }

    @Test
    @DisplayName("fulfillReservation: should mark as fulfilled and save")
    void fulfillReservation_success() {
        Reservation reservation = new Reservation();
        reservation.setFulfilled(false);

        reservationService.fulfillReservation(reservation);

        assertTrue(reservation.isFulfilled());
        verify(reservationRepository).save(reservation);
    }
}
