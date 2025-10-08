package com.exadel.bookService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {
    private Long bookId;
    private Long userId;
    private int loanDays = 14; // valor default
}

