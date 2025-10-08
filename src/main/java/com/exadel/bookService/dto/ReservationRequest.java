package com.exadel.bookService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private Long bookId;
    private Long userId;
}
