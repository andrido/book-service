package com.exadel.bookService.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String author;

    @Column(unique = true)
    private String isbn;

    @Column
    private boolean available;
    @Column
    private Integer quantity;




}

