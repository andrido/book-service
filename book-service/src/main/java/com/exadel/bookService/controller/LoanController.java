package com.exadel.bookService.controller;

import com.exadel.bookService.model.Loan;
import com.exadel.bookService.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Criar empréstimo
    @PostMapping
    public Loan borrowBook(@RequestParam Long bookId,
                           @RequestParam Long userId,
                           @RequestParam(defaultValue = "14") int loanDays) {
        return loanService.borrowBook(bookId, userId, loanDays);
    }

    // Devolver livro
    @PutMapping("/{loanId}/return")
    public Loan returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }

    // Listar todos os empréstimos de um usuário
    @GetMapping("/user/{userId}")
    public List<Loan> getLoansByUser(@PathVariable Long userId) {
        return loanService.getLoansByUser(userId);
    }

    // Listar apenas empréstimos ativos
    @GetMapping("/user/{userId}/active")
    public List<Loan> getActiveLoansByUser(@PathVariable Long userId) {
        return loanService.getActiveLoansByUser(userId);
    }
}
