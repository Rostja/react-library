package com.eshop.spring_boot_library.service;

import com.eshop.spring_boot_library.dao.BookRepository;
import com.eshop.spring_boot_library.dao.CheckoutRepository;
import com.eshop.spring_boot_library.dao.ReviewRepository;
import com.eshop.spring_boot_library.entity.Book;
import com.eshop.spring_boot_library.requestmodels.AddBookRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceTest{

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CheckoutRepository checkoutRepository;

    private AdminService adminService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adminService = new AdminService(bookRepository, reviewRepository, checkoutRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void increaseBookQuantity_Success() throws Exception {
        //given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(1);
        book.setCopies(1);

        //when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        adminService.increaseBookQuantity(bookId);

        //then
        assertEquals(2, book.getCopiesAvailable());
        assertEquals(2, book.getCopies());
        verify(bookRepository).save(book);
    }

    @Test
    void increaseBookQuantity_throwsException_WhenBookNotFound(){
        //Given
        Long bookId =1L;

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //Then
        Exception exception = assertThrows(Exception.class, () -> {
            adminService.increaseBookQuantity(bookId);
        });
        assertTrue(exception.getMessage().contains("Book not found"));
    }

    @Test
    void decreaseBookQuantity_Success() throws Exception {
        //given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(2);
        book.setCopies(2);

        //when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        //then
        adminService.decreaseBookQuantity(bookId);
        assertEquals(1, book.getCopiesAvailable());
        assertEquals(1, book.getCopies());
        verify(bookRepository).save(book);
    }

    @Test
    void decreaseBookQuantity_throwsException_WhenBookNotFound(){
        //Given
        Long bookId = 1L;

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //Then
        Exception exception = assertThrows(Exception.class, () -> {
            adminService.decreaseBookQuantity(bookId);
        });
        assertTrue(exception.getMessage().contains("Book not found or quantity locked"));
    }

    @Test
    void decreaseBookQuantity_ThrowsException_WhenQuantityZero(){
        //Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(0);
        book.setCopies(0);

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        //Then
        Exception exception = assertThrows(Exception.class, () -> {
            adminService.decreaseBookQuantity(bookId);
        });
        assertTrue(exception.getMessage().contains("Book not found or quantity locked"));
    }

    @Test
    void postBook_Success() {
        //Given
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setCopiesAvailable(10);
        book.setCopies(10);

        //When
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        adminService.postBook(book);

        //Then
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_Success() throws Exception {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);


        // When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        adminService.deleteBook(bookId);

        // Then
        verify(bookRepository).delete(book);
        verify(checkoutRepository).deleteAllByBookId(bookId);
        verify(reviewRepository).deleteAllByBookId(bookId);
    }

    @Test
    void deleteBook_ThrowsException_WhenBookNotFound(){
        //Given
        Long bookId = 1L;

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //Then
        Exception exception = assertThrows(Exception.class, () -> {
            adminService.deleteBook(bookId);
        });

        //Then
        assertTrue(exception.getMessage().contains("Book not found"));
    }
}