package com.exadel.bookService.validation;

import com.exadel.bookService.exception.BookValidationException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.repository.BookRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BookValidator {

    private final BookRepository repository;

    public BookValidator(BookRepository repository) {
        this.repository = repository;
    }

    public void validate(Book book) {
        if (!StringUtils.hasText(book.getTitle())) {
            throw new BookValidationException("Title is required");
        }

        if (!StringUtils.hasText(book.getIsbn())) {
            throw new BookValidationException("ISBN is required");
        }

        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BookValidationException("ISBN already exists");
        }
    }

    public void validateForUpdate(Book updates, Book existing) {

        if (updates.getTitle() != null && !StringUtils.hasText(updates.getTitle())) {
            throw new BookValidationException("Title cannot be blank");
        }

        // ISBN
        if (updates.getIsbn() != null) {
            if (!StringUtils.hasText(updates.getIsbn())) {
                throw new BookValidationException("ISBN cannot be blank");
            }

            if (!updates.getIsbn().equals(existing.getIsbn())
                    && repository.existsByIsbn(updates.getIsbn())) {

                throw new BookValidationException("ISBN already exists");
            }
        }
    }
}
