package com.exadel.bookService.service;

import com.exadel.bookService.dto.LoanRequest;
import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.LoanNotFoundException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.model.Loan;
import com.exadel.bookService.model.LoanStatus;
import com.exadel.bookService.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService implements ILoanService {

    private final LoanRepository loanRepository;
    private final IBookService bookService; // injeta a interface de BookService

    public LoanService(LoanRepository loanRepository, IBookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    @Transactional
    @Override
    public Loan borrowBook(LoanRequest loanRequest) {
        Long bookId = loanRequest.getBookId();
        Long userId = loanRequest.getUserId();
        var book = bookService.getBookById(bookId);

        int updated = bookService.decrementIfAvailable(bookId);
        if (updated == 0) {
            throw new BookNotFoundException("Book not available for loan. You can create a reservation.");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUserId(userId);
        loan.setBorrowedAt(java.time.LocalDateTime.now());
        loan.setDueAt(java.time.LocalDateTime.now().plusDays(14));
        loan.setStatus(com.exadel.bookService.model.LoanStatus.BORROWED);

        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new com.exadel.bookService.exception.LoanNotFoundException("Loan not found"));

        if (loan.getStatus() != com.exadel.bookService.model.LoanStatus.BORROWED) {
            throw new IllegalStateException("Loan is not active");
        }

        loan.setReturnedAt(java.time.LocalDateTime.now());
        loan.setStatus(com.exadel.bookService.model.LoanStatus.RETURNED);

        bookService.incrementQuantity(loan.getBook().getId());

        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    @Override
    public List<Loan> getActiveLoansByUser(Long userId) {
        return loanRepository.findByUserIdAndStatus(userId, com.exadel.bookService.model.LoanStatus.BORROWED);
    }
}
