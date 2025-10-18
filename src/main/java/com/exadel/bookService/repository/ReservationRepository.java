package com.exadel.bookService.repository;

import com.exadel.bookService.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>
{
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByUserId(Long userId);

}
