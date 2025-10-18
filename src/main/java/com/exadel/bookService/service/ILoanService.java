package com.exadel.bookService.service;

import com.exadel.bookService.dto.LoanRequest;
import com.exadel.bookService.model.Loan;

import java.util.List;

public interface ILoanService {
    Loan borrowBook(LoanRequest request);
    Loan returnBook(Long loanId);
    List<Loan> getLoansByUser(Long userId);
    List<Loan> getActiveLoansByUser(Long userId);
}
