package com.exadel.bookService.validation;

import com.exadel.bookService.exception.BookValidationException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.repository.BookRepository;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {

    private final BookRepository repository;

    public BookValidator(BookRepository repository) {
        this.repository = repository;
    }

    public void validate(Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new BookValidationException("Title is required");
        }

        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new BookValidationException("ISBN is required");
        }

        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BookValidationException("ISBN already exists");
        }
    }

    public void validateForUpdate(Book book) {
        if (book.getTitle() != null && book.getTitle().isBlank()) {
            throw new BookValidationException("Title cannot be blank");
        }
        if (book.getIsbn() != null && book.getIsbn().isBlank()) {
            throw new BookValidationException("ISBN cannot be blank");
        }
    }



}
