package com.exadel.bookService.service;

import com.exadel.bookService.model.Book;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    List<Book> getAllBooks();

    Book getBookById(Long id);

    Book createBook(Book book);

    Book updateBook(Long id, Book book);

    void deleteBook(Long id);

    @Transactional
    int decrementIfAvailable(Long bookId);

    @Transactional
    void incrementQuantity(Long bookId);
}
