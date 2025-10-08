package com.exadel.bookService.repository;

import com.exadel.bookService.model.Loan;
import com.exadel.bookService.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByIdAndStatus(Long id, LoanStatus status);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);
    List<Loan> findByUserId(Long userId);
}