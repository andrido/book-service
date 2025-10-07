package com.exadel.bookService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter @Setter
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



    @Override
    public String toString() {
        return "Book [" +
                "id=" + id
                + ", title=" + title + ","
                + " author=" + author + ","
                + " isbn=" + isbn + ","
                + " available=" + available
                + ", quantity=" + quantity + "" +
                "]";
    }
}

