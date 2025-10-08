package com.exadel.bookService.controller;

import com.exadel.bookService.dto.LoanRequest;
import com.exadel.bookService.dto.ReturnRequest;
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
    public Loan borrowBook(@RequestBody LoanRequest request) {
        return loanService.borrowBook(
                request.getBookId(),
                request.getUserId(),
                request.getLoanDays()
        );
    }

    // Devolver livro

    @PutMapping("/return")
    public Loan returnBook(@RequestBody ReturnRequest request) {
        return loanService.returnBook(request.getLoanId());
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
