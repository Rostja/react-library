package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setTitle("Animals");
        book1.setAuthor("James Robinson");
        book1.setDescription("Description1");
        book1.setCopies(5);
        book1.setCopiesAvailable(3);
        book1.setCategory("Natural Sciences");
        book1.setImg("image1.jpg");

        book2 = new Book();
        book2.setTitle("India");
        book2.setAuthor("Rahul Prandjawal");
        book2.setDescription("Description2");
        book2.setCopies(3);
        book2.setCopiesAvailable(2);
        book2.setCategory("Geography");
        book2.setImg("image2.jpg");

        book3 = new Book();
        book3.setTitle("Aliens");
        book3.setAuthor("Wojciech Namarowsky");
        book3.setDescription("Description3");
        book3.setCopies(4);
        book3.setCopiesAvailable(4);
        book3.setCategory("Fiction");
        book3.setImg("image3.jpg");

        bookRepository.saveAll(Arrays.asList(book1, book2, book3));

    }


    @Test
    void findByTitleContaining() {
        Pageable pageable = PageRequest.of(0,10);

        //finding Animals in title
        Page<Book> books = bookRepository.findByTitleContaining("Animals", pageable);
        assertEquals(1, books.getContent().size());
        assertTrue(books.getContent().contains(book1));
        assertFalse(books.getContent().contains(book2));

        //finding NonExistent in title
        Page<Book> anotherBooks = bookRepository.findByTitleContaining("NonExistent", pageable);
        assertEquals(0,anotherBooks.getContent().size());
    }

    @Test
    void findByCategory() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Book> geographyBooks = bookRepository.findByCategory("Geography", pageable);
        Page<Book> fictionBooks = bookRepository.findByCategory("Fiction", pageable);
        Page<Book> nonExistentCategory = bookRepository.findByCategory("NonExistent", pageable);

        // Then
        assertEquals(1, geographyBooks.getTotalElements());
        assertEquals("India", geographyBooks.getContent().get(0).getTitle());

        assertEquals(1, fictionBooks.getTotalElements());
        assertEquals("Aliens", fictionBooks.getContent().get(0).getTitle());

        assertEquals(0, nonExistentCategory.getTotalElements());
    }

    @Test
    void findBooksByBookIds() {
        //Given
        List<Long> bookIds = Arrays.asList(book1.getId(),book2.getId(), book3.getId());

        //When
        List<Book> foundBooks = bookRepository.findBooksByBookIds(bookIds);

        //Then
        assertEquals(3,foundBooks.size());
        assertTrue(foundBooks.stream().anyMatch(book -> book.getTitle().equals("Animals")));
        assertTrue(foundBooks.stream().anyMatch(book -> book.getTitle().equals("India")));
    }

    @Test
    void findBooksByBookIds_WithNonExistentIds(){
        //Given
        List<Long> bookIds = Arrays.asList(999L, 888L);

        //When
        List<Book> foundBooks = bookRepository.findBooksByBookIds(bookIds);

        //Then
        assertTrue(foundBooks.isEmpty());
    }

    @Test
    void findBooksByTitleContaining_WithNonExistentTitle(){
        //Given
        String title = "NonExistent";

        //When
        Page<Book> books = bookRepository.findByTitleContaining(title, PageRequest.of(0,10));

        //Then
        assertEquals(0, books.getTotalElements());
    }



    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }
}