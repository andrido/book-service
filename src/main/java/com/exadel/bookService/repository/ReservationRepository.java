package com.exadel.bookService.repository;

import com.exadel.bookService.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>
{
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByUserId(Long userId);
    @Query("SELECT r.userName, r.userEmail FROM Reservation r WHERE r.book.id = :bookId AND r.fulfilled = false")
    List<Object[]> findUserNamesAndEmailsByBookId(@Param("bookId") Long bookId);

}
