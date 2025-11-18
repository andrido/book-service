package com.exadel.bookService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanEvent {
    private Long loanId;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String status;
}
