package com.exadel.bookService.service;

import com.exadel.bookService.exception.BookNotFoundException;
import com.exadel.bookService.exception.BookValidationException;
import com.exadel.bookService.model.Book;
import com.exadel.bookService.repository.BookRepository;
import com.exadel.bookService.validation.BookValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookValidator validator;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("getAllBooks: should return all books")
    void getAllBooks_returnsAllBooks() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book One");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book Two");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 books");
        assertEquals("Book One", result.get(0).getTitle());
        assertEquals("Book Two", result.get(1).getTitle());
        verify(bookRepository, times(1)).findAll(); // verifica que o repository foi chamado
    }

    @Test
    @DisplayName("getBookById: should return book when ID exists)")
    void getBookById_existingId_returnsBook() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertNotNull(result, "Result should not be null when the ID exists");
        assertEquals("Test Book", result.getTitle(), "Book title does not match expected value");
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getBookById: non-existing ID should throw exception")
    void getBookById_nonExistingId_throwsException() {
    // Arrange
    when(bookRepository.findById(2L)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class, () -> {
        bookService.getBookById(2L);
    }, "Expected getBookById to throw an exception for non-existing ID");

    assertTrue(exception.getMessage().contains("not found"), "Exception message should indicate book not found");
    verify(bookRepository, times(1)).findById(2L);
}

    @Test
    @DisplayName("getBookById: null ID should throw IllegalArgumentException")
    void getBookById_nullId_throwsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.getBookById(null);
        }, "Expected getBookById to throw IllegalArgumentException for null ID");

        assertEquals("ID cannot be null", exception.getMessage(), "Exception message should indicate null ID");
        verify(bookRepository, never()).findById(any()); // nunca deve chamar o repository
    }

    @Test
    @DisplayName("createBook: should validate and save book")
    void createBook_savesBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Valid Title");
        book.setIsbn("1234567890");

        when(bookRepository.save(book)).thenReturn(book);

        // Act
        Book result = bookService.createBook(book);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Valid Title", result.getTitle(), "Book title does not match expected value");
        verify(validator, times(1)).validate(book);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("createBook: should throw BookValidationException if title is null or blank")
    void createBook_titleMissing_throwsException() {
        // Arrange
        Book book = new Book();
        book.setIsbn("1234567890");
        doThrow(new BookValidationException("Title is required")).when(validator).validate(book);

        // Act & Assert
        BookValidationException exception = assertThrows(BookValidationException.class,
                () -> bookService.createBook(book));
        assertEquals("Title is required", exception.getMessage());
        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("createBook: should throw BookValidationException if ISBN is null or blank")
    void createBook_isbnMissing_throwsException() {
        // Arrange
        Book book = new Book();
        book.setTitle("Valid Title");
        doThrow(new BookValidationException("ISBN is required")).when(validator).validate(book);

        // Act & Assert
        BookValidationException exception = assertThrows(BookValidationException.class,
                () -> bookService.createBook(book));
        assertEquals("ISBN is required", exception.getMessage());
        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("createBook: should throw BookValidationException if ISBN already exists")
    void createBook_isbnExists_throwsException() {
        // Arrange
        Book book = new Book();
        book.setTitle("Valid Title");
        book.setIsbn("1234567890");
        doThrow(new BookValidationException("ISBN already exists")).when(validator).validate(book);

        // Act & Assert
        BookValidationException exception = assertThrows(BookValidationException.class,
                () -> bookService.createBook(book));
        assertEquals("ISBN already exists", exception.getMessage());
        verify(bookRepository, never()).save(book);
    }


    @Test
    @DisplayName("updateBook: should update existing book")
    void updateBook_existingBook_updatesFields() {
        Book existing = new Book();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setIsbn("123");

        Book updates = new Book();
        updates.setTitle("New Title");
        updates.setIsbn("123");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Book result = bookService.updateBook(1L, updates);

        assertEquals("New Title", result.getTitle());


        verify(validator, times(1)).validateForUpdate(updates, existing);

        verify(bookRepository, times(1)).save(existing);
    }


    @Test
    @DisplayName("updateBook: non-existing book should throw exception")
    void updateBook_nonExistingBook_throwsException() {
        Book updates = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, updates));
    }

    // ---------- DELETE BOOK ----------
    @Test
    @DisplayName("deleteBook: should delete existing book")
    void deleteBook_existingBook_deletes() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteBook: non-existing book should throw exception")
    void deleteBook_nonExistingBook_throwsException() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).deleteById(any());
    }

    // ---------- DECREMENT IF AVAILABLE ----------
    @Test
    @DisplayName("decrementIfAvailable: should decrement when quantity > 0")
    void decrementIfAvailable_quantityPositive_decrements() {
        Book book = new Book();
        book.setId(1L);
        book.setQuantity(5);
        book.setAvailable(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        int result = bookService.decrementIfAvailable(1L);

        assertEquals(1, result);
        assertEquals(4, book.getQuantity());
        assertTrue(book.isAvailable());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("decrementIfAvailable: should return 0 if quantity <= 0")
    void decrementIfAvailable_quantityZero_returnsZero() {
        Book book = new Book();
        book.setId(1L);
        book.setQuantity(0);
        book.setAvailable(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        int result = bookService.decrementIfAvailable(1L);

        assertEquals(0, result);
        verify(bookRepository, never()).save(book);
    }

    // ---------- INCREMENT QUANTITY ----------
    @Test
    @DisplayName("incrementQuantity: should increase quantity and set available true")
    void incrementQuantity_increasesQuantity() {
        Book book = new Book();
        book.setId(1L);
        book.setQuantity(2);
        book.setAvailable(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.incrementQuantity(1L);

        assertEquals(3, book.getQuantity());
        assertTrue(book.isAvailable());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("incrementQuantity: non-existing book should throw exception")
    void incrementQuantity_nonExistingBook_throwsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.incrementQuantity(1L));
        verify(bookRepository, never()).save(any());
    }
}




