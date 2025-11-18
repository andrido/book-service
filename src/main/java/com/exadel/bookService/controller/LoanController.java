package com.exadel.bookService.controller;

import com.exadel.bookService.dto.LoanRequest;
import com.exadel.bookService.dto.ReturnRequest;
import com.exadel.bookService.model.Loan;
import com.exadel.bookService.service.ILoanService;
import com.exadel.bookService.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;




    @PostMapping
    public Loan borrowBook(@RequestBody @Valid LoanRequest request) {
        return loanService.borrowBook(request);
    }


    @PutMapping("/return")
    public Loan returnBook(@RequestBody ReturnRequest request) {
        return loanService.returnBook(request.getLoanId());
    }



    @GetMapping("/user/{userId}")
    public List<Loan> getLoansByUser(@PathVariable Long userId) {
        return loanService.getLoansByUser(userId);
    }


    @GetMapping("/user/{userId}/active")
    public List<Loan> getActiveLoansByUser(@PathVariable Long userId) {
        return loanService.getActiveLoansByUser(userId);
    }
}
