package com.exadel.bookService.service;

import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.repository.BookRepository;
import com.exadel.bookService.validation.BookValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;
    private final BookValidator validator;

    public BookService(BookRepository repository, BookValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return repository.findById(id);
    }

    public Book createBook(Book book) {
        validator.validate(book);  // validação isolada
        return repository.save(book);
    }

    public Book updateBook(Long id, Book book) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        validator.validateForUpdate(existing, book);

        if (book.getTitle() != null) existing.setTitle(book.getTitle());
        if (book.getAuthor() != null) existing.setAuthor(book.getAuthor());
        if (book.getIsbn() != null) existing.setIsbn(book.getIsbn());
        if (book.getQuantity() != null) existing.setQuantity(book.getQuantity());
        existing.setAvailable(book.isAvailable());

        return repository.save(existing);

    }

    public void deleteBook(Long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException("Book not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public int decrementIfAvailable(Long bookId) {
        return repository.decrementIfAvailable(bookId);
    }

    @Transactional
    public int incrementQuantity(Long bookId) {
        return repository.incrementQuantity(bookId);
    }
}
