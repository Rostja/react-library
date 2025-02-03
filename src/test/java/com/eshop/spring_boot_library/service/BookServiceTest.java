package com.eshop.spring_boot_library.service;

import com.eshop.spring_boot_library.dao.BookRepository;
import com.eshop.spring_boot_library.dao.CheckoutRepository;
import com.eshop.spring_boot_library.dao.HistoryRepository;
import com.eshop.spring_boot_library.dao.PaymentRepository;
import com.eshop.spring_boot_library.entity.Book;
import com.eshop.spring_boot_library.entity.Checkout;
import com.eshop.spring_boot_library.responsemodels.ShelfCurrentLoansResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private HistoryRepository historyRepository;
    @Mock
    private PaymentRepository paymentRepository;

    private BookService bookService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        bookService = new BookService(bookRepository, checkoutRepository, historyRepository, paymentRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    public void close() throws Exception{
        if (autoCloseable != null){
            autoCloseable.close();
        }
    }

    @Test
    void checkoutBook_Success() throws Exception{
        //given
        String userEmail = "test@email.com";
        String title = "Make It";
        String author = "Craig Malstein";
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(1);
        book.setTitle(title);
        book.setAuthor(author);

        //when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(checkoutRepository.findByUserEmailAndBookId(userEmail,bookId)).thenReturn(null);
        when(checkoutRepository.findBooksByUserEmail(userEmail)).thenReturn(new ArrayList<>());

        Book actualBook = bookService.checkoutBook(userEmail, bookId);

        //then
        assertNotNull(actualBook);
        assertEquals(bookId, actualBook.getId());
        assertEquals(0, actualBook.getCopiesAvailable());
        verify(bookRepository, times(1)).save(book);
        verify(bookRepository).save(any(Book.class));
        verify(checkoutRepository, times(1)).save(any(com.eshop.spring_boot_library.entity.Checkout.class));
    }

    @Test
    void checkoutBook_ThrowsException_WhenBookNotAvailable(){
        //Given
        String userEmail = "test@email.com";
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(0);

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        //Then
        Exception exception = assertThrows(Exception.class,() ->{
            bookService.checkoutBook(userEmail,bookId);
        });

        assertTrue(exception.getMessage().contains("Book does not exist or already checked out by user"));
    }

    @Test
    void checkoutBookByUser_ReturnsTrue_WhenBooksIsCheckedOut() {
        //Given
        String userEmail = "test@email.com";
        List<Checkout> checkouts = new ArrayList<>();
        Checkout checkout = new Checkout();
        checkout.setUserEmail(userEmail);
        checkouts.add(checkout);

        //When
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, checkout.getBookId())).thenReturn(checkout);

        //Then
        boolean actual = bookService.checkoutBookByUser(userEmail, checkout.getBookId());
        assertTrue(actual);
    }

    @Test
    void checkoutBookByUser_ReturnsFalse_WhenBookIsNotCheckedOut() {
        // Given
        String userEmail = "test@email.com";
        Long bookId = 1L;

        // When
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId))
                .thenReturn(null);
        boolean result = bookService.checkoutBookByUser(userEmail, bookId);

        // Then
        assertFalse(result);
    }

    @Test
    void currentLoansCount_ReturnsCorrectCount() {
        // Given
        String userEmail = "test@email.com";
        String userEmail2 = "test2@email.com";
        List<Checkout> checkouts = new ArrayList<>();
        Checkout checkout = new Checkout();
        Checkout checkout2 = new Checkout();
        checkout.setUserEmail(userEmail);
        checkout2.setUserEmail(userEmail2);
        checkouts.add(checkout);
        checkouts.add(checkout2);

        // When
        when(checkoutRepository.findBooksByUserEmail(userEmail)).thenReturn(checkouts);

        // Then
        int actual = bookService.currentLoansCount(userEmail);
        assertEquals(2, actual);
    }

    @Test
    void currentLoans_ReturnsCorrectLoans() throws Exception {
        //Given
        String userEmail = "test@email.com";
        List<Checkout> checkouts = new ArrayList<>();
        Checkout checkout = new Checkout(userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                1L);
        checkouts.add(checkout);

        Book book = new Book();
        book.setId(1L);
        List<Book> books = List.of(book);

        //When
        when(checkoutRepository.findBooksByUserEmail(userEmail)).thenReturn(checkouts);
        when(bookRepository.findBooksByBookIds(any())).thenReturn(books);

        //Then
        List<ShelfCurrentLoansResponse> actual = bookService.currentLoans(userEmail);
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1,actual.size());
    }

    @Test
    void returnBook() throws Exception {
        // Given
        String userEmail = "test@email.com";
        Long bookId = 1L;
        Checkout checkout = new Checkout(userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                bookId);
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(0);

        // When
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(checkout);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Then
        bookService.returnBook(userEmail, bookId);
        verify(bookRepository).save(book);
        assertEquals(1,book.getCopiesAvailable());
    }

    @Test
    void renewLoan_Success() throws Exception {
        //Given
        String userEmail = "test@email.com";
        Long bookId = 1L;
        Checkout checkout = new Checkout(userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                bookId);

        // When
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(checkout);

        // Then
        bookService.renewLoan(userEmail, bookId);
        verify(checkoutRepository).save(any(Checkout.class));
    }
}