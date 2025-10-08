package com.exadel.bookService.service;

import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.LoanNotFoundException;
import com.exadel.bookService.model.Loan;
import com.exadel.bookService.model.LoanStatus;
import com.exadel.bookService.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    @Transactional
    public Loan borrowBook(Long bookId, Long userId, int loanDays) {
        // decrementa quantidade do livro se disponível
        int updated = bookService.decrementIfAvailable(bookId);
        if (updated == 0) {
            throw new BookNotFoundException("Book not available for loan");
        }

        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setUserId(userId);
        loan.setBorrowedAt(LocalDateTime.now());
        loan.setDueAt(LocalDateTime.now().plusDays(loanDays));
        loan.setStatus(LoanStatus.BORROWED);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.BORROWED) {
            throw new IllegalStateException("Loan is not active");
        }

        loan.setReturnedAt(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);

        // incrementa quantidade do livro
        bookService.incrementQuantity(loan.getBookId());

        return loanRepository.save(loan);
    }

    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getActiveLoansByUser(Long userId) {
        return loanRepository.findByUserIdAndStatus(userId, LoanStatus.BORROWED);
    }
}
