package com.exadel.bookService.repository;

import com.exadel.bookService.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    @Modifying
    @Query("update Book b set b.quantity = b.quantity - 1 where b.id = :id and b.quantity > 0")
    int decrementIfAvailable(@Param("id") Long id);

    @Modifying
    @Query("update Book b set b.quantity = b.quantity + 1 where b.id = :id")
    int incrementQuantity(@Param("id") Long id);
}
