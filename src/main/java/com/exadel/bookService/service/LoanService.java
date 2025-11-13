package com.exadel.bookService.service;

import com.exadel.bookService.dto.LoanRequest;
import com.exadel.bookService.dto.LoanEvent;
import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.LoanNotFoundException;
import com.exadel.bookService.model.Loan;
import com.exadel.bookService.model.LoanStatus;
import com.exadel.bookService.repository.LoanRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService implements ILoanService {

    private final LoanRepository loanRepository;
    private final IBookService bookService;
    private final KafkaTemplate<String, LoanEvent> kafkaTemplate; // 🔄 agora usa o DTO

    public LoanService(LoanRepository loanRepository, IBookService bookService,
                       KafkaTemplate<String, LoanEvent> kafkaTemplate) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.kafkaTemplate = kafkaTemplate;
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
        loan.setBorrowedAt(LocalDateTime.now());
        loan.setDueAt(LocalDateTime.now().plusDays(14));
        loan.setStatus(LoanStatus.BORROWED);

        Loan savedLoan = loanRepository.save(loan);

        // 📤 Publica o evento no Kafka com o DTO
        LoanEvent event = new LoanEvent(
                savedLoan.getId(),
                userId,
                bookId,
                book.getTitle(),
                savedLoan.getStatus().name()
        );

        kafkaTemplate.send("loan-events", event);

        return savedLoan;
    }

    @Transactional
    @Override
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.BORROWED) {
            throw new IllegalStateException("Loan is not active");
        }

        loan.setReturnedAt(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);

        bookService.incrementQuantity(loan.getBook().getId());

        Loan savedLoan = loanRepository.save(loan);

        // 📤 Publica o evento de devolução também como DTO
        LoanEvent event = new LoanEvent(
                savedLoan.getId(),
                savedLoan.getUserId(),
                savedLoan.getBook().getId(),
                savedLoan.getBook().getTitle(),
                savedLoan.getStatus().name()
        );

        kafkaTemplate.send("loan-events", event);

        return savedLoan;
    }

    @Override
    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    @Override
    public List<Loan> getActiveLoansByUser(Long userId) {
        return loanRepository.findByUserIdAndStatus(userId, LoanStatus.BORROWED);
    }
}
