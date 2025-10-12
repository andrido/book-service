package com.exadel.bookService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    @NotNull(message = "bookId is required")
    private Long bookId;
    @NotNull(message = "userId is required")
    private Long userId;
}
