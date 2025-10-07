package com.exadel.bookService.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter

public class LoanRequest {
    @NotNull(message = "bookId is required")
    private Long bookId;
    @NotNull(message = "userId is required")
    private Long userId;

}

