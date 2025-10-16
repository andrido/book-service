package com.exadel.bookService.service;

import com.exadel.bookService.dto.LoanRequest;
import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.LoanNotFoundException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.model.Loan;
import com.exadel.bookService.model.LoanStatus;
import com.exadel.bookService.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("borrowBook: should create loan successfully")
    void borrowBook_createsLoanSuccessfully() {
        LoanRequest request = new LoanRequest();
        request.setBookId(1L);
        request.setUserId(2L);

        Book book = new Book();
        book.setId(1L);
        book.setQuantity(1);

        when(bookService.getBookById(1L)).thenReturn(book);
        when(bookService.decrementIfAvailable(1L)).thenReturn(1);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.borrowBook(request);

        assertNotNull(result);
        assertEquals(LoanStatus.BORROWED, result.getStatus());
        assertEquals(book, result.getBook());
        verify(bookService).decrementIfAvailable(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    @DisplayName("borrowBook: should throw exception if book unavailable")
    void borrowBook_bookUnavailable_throwsException() {
        LoanRequest request = new LoanRequest();
        request.setBookId(1L);
        request.setUserId(2L);

        Book book = new Book();
        book.setId(1L);
        book.setQuantity(0);

        when(bookService.getBookById(1L)).thenReturn(book);
        when(bookService.decrementIfAvailable(1L)).thenReturn(0);

        assertThrows(BookNotFoundException.class, () -> loanService.borrowBook(request));
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("returnBook: should return loan successfully")
    void returnBook_success() {
        Book book = new Book();
        book.setId(1L);

        Loan loan = new Loan();
        loan.setId(10L);
        loan.setBook(book);
        loan.setStatus(LoanStatus.BORROWED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.returnBook(10L);

        assertNotNull(result.getReturnedAt());
        assertEquals(LoanStatus.RETURNED, result.getStatus());
        verify(bookService).incrementQuantity(1L);
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("returnBook: should throw LoanNotFoundException if not found")
    void returnBook_notFound_throwsException() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook(1L));
        verify(bookService, never()).incrementQuantity(anyLong());
    }

    @Test
    @DisplayName("returnBook: should throw IllegalStateException if loan not active")
    void returnBook_notActive_throwsException() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class, () -> loanService.returnBook(1L));
        verify(bookService, never()).incrementQuantity(anyLong());
    }

    @Test
    @DisplayName("getLoansByUser: should return user loans")
    void getLoansByUser_returnsLoans() {
        Loan loan = new Loan();
        when(loanRepository.findByUserId(2L)).thenReturn(List.of(loan));

        List<Loan> result = loanService.getLoansByUser(2L);

        assertEquals(1, result.size());
        verify(loanRepository).findByUserId(2L);
    }

    @Test
    @DisplayName("getActiveLoansByUser: should return borrowed loans")
    void getActiveLoansByUser_returnsActiveLoans() {
        Loan loan = new Loan();
        when(loanRepository.findByUserIdAndStatus(2L, LoanStatus.BORROWED)).thenReturn(List.of(loan));

        List<Loan> result = loanService.getActiveLoansByUser(2L);

        assertEquals(1, result.size());
        verify(loanRepository).findByUserIdAndStatus(2L, LoanStatus.BORROWED);
    }
}