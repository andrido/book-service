package com.exadel.bookService.service;

import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.BookValidationException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.repository.BookRepository;
import com.exadel.bookService.validation.BookValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService implements IBookService {

    private final BookRepository repository;
    private final BookValidator validator;

    public BookService(BookRepository repository, BookValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }


    @Override
    public Book createBook(Book book) {
        validator.validate(book);
        return repository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book updates) {
        if (updates == null) {
            throw new IllegalArgumentException("Book updates cannot be null");
        }

        Book existing = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        // Valida apenas os dados que vieram na requisição
        validator.validateForUpdate(updates);

        // Verifica o ISBN no banco, se foi enviado e não é o mesmo do existente
        if (updates.getIsbn() != null && !updates.getIsbn().equals(existing.getIsbn())) {
            if (repository.existsByIsbn(updates.getIsbn())) {
                throw new BookValidationException("ISBN already exists");
            }
            existing.setIsbn(updates.getIsbn());
        }

        // Atualiza outros campos se vierem
        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getAuthor() != null) existing.setAuthor(updates.getAuthor());
        if (updates.getQuantity() != null) existing.setQuantity(updates.getQuantity());
        existing.setAvailable(updates.isAvailable());

        return repository.save(existing);
    }

    @Override
    public void deleteBook(Long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException("Book not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public int decrementIfAvailable(Long bookId) {
        Book book = repository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getQuantity() <= 0) return 0;

        book.setQuantity(book.getQuantity() - 1);
        book.setAvailable(book.getQuantity() > 0);

        repository.save(book);
        return 1;
    }

    @Transactional
    @Override
    public void incrementQuantity(Long bookId) {
        Book book = repository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        book.setQuantity(book.getQuantity() + 1);
        book.setAvailable(true);

        repository.save(book);
    }


}