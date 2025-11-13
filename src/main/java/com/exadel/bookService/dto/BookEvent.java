package com.exadel.bookService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEvent {
    private Long bookId;
    private String title;
    private String status; // AVAILABLE, UNAVAILABLE, etc
    private Long userId;
    private String userName;
    private String userEmail;

}
