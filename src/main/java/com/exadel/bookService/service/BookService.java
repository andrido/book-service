package com.exadel.bookService.service;

import com.exadel.bookService.model.Reservation;
import org.springframework.util.StringUtils;
import com.exadel.bookService.client.UserClient;
import com.exadel.bookService.dto.BookEvent;
import com.exadel.bookService.dto.BookStatus;
import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.kafka.BookEventProducer;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.repository.BookRepository;
import com.exadel.bookService.repository.ReservationRepository;
import com.exadel.bookService.validation.BookValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService implements IBookService {

    private final BookRepository repository;
    private final BookValidator validator;
    private final BookEventProducer bookEventProducer;
    private final ReservationRepository reservationRepository;
    private final UserClient userClient;

    public BookService(BookRepository repository,
                       BookValidator validator,
                       BookEventProducer bookEventProducer,
                       ReservationRepository reservationRepository,
                       UserClient userClient) {
        this.repository = repository;
        this.validator = validator;
        this.bookEventProducer = bookEventProducer;
        this.reservationRepository = reservationRepository;
        this.userClient = userClient;
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

        validator.validateForUpdate(updates, existing);

        applyUpdates(existing, updates);

        return repository.save(existing);
    }

    private void applyUpdates(Book existing, Book updates) {
        updateTitle(existing, updates);
        updateAuthor(existing, updates);
        updateIsbn(existing, updates);
        updateQuantity(existing, updates);
        updateAvailability(existing, updates);
    }

    private void updateTitle(Book existing, Book updates) {
        if (StringUtils.hasText(updates.getTitle())) {
            existing.setTitle(updates.getTitle());
        }
    }

    private void updateAuthor(Book existing, Book updates) {
        if (StringUtils.hasText(updates.getAuthor())) {
            existing.setAuthor(updates.getAuthor());
        }
    }

    private void updateIsbn(Book existing, Book updates) {
        if (updates.getIsbn() != null && !updates.getIsbn().equals(existing.getIsbn())) {
            existing.setIsbn(updates.getIsbn());
        }
    }

    private void updateQuantity(Book existing, Book updates) {
        if (updates.getQuantity() != null) {
            existing.setQuantity(updates.getQuantity());
        }
    }

    private void updateAvailability(Book existing, Book updates) {
        existing.setAvailable(updates.isAvailable());
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

    public void incrementQuantity(Long bookId) {
        Book book = repository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        boolean wasUnavailable = book.getQuantity() == 0;

        book.setQuantity(book.getQuantity() + 1);
        book.setAvailable(true);
        repository.save(book);


        if (wasUnavailable) {
            processReservationsFor(book);
        }
    }

    private void processReservationsFor(Book book) {
        var reservations = reservationRepository.findByBookId(book.getId());


        reservations.forEach(r -> {

            if (!r.isFulfilled()) {
                notifyUserAndFulfillReservation(book, r);
            }
        });
    }
    private void notifyUserAndFulfillReservation(Book book, Reservation r) {

            var userResponse = userClient.getUserById(r.getUserId());

            BookEvent event = new BookEvent(
                    book.getId(),
                    book.getTitle(),
                    BookStatus.AVAILABLE,
                    r.getUserId(),
                    userResponse.getFirstName(),
                    userResponse.getEmail()
            );

            bookEventProducer.sendBookEvent(event);

            r.setFulfilled(true);
            reservationRepository.save(r);

    }
}