package com.exadel.bookService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRequest {
    @NotNull(message = "loanId is required")
    private Long loanId;
}
