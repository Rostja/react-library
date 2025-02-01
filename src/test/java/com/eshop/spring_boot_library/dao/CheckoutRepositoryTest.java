package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.Checkout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CheckoutRepositoryTest {

    @Autowired
    private CheckoutRepository checkoutRepository;

    private Checkout checkout1;
    private Checkout checkout2;
    private Checkout checkout3;
    private Checkout checkout4;

    @BeforeEach
    void setUp() {

        checkout1 = new Checkout(
                "user1@example.com",
                "2024-01-25",
                "2024-03-14",
                1L
        );

        checkout2 = new Checkout(
                "user2@example.com",
                "2024-02-15",
                "2024-04-04",
                2L
        );

        checkout3 = new Checkout(
                "user3@example.com",
                "2024-03-05",
                "2024-05-14",
                3L
        );

        checkout4 = new Checkout(
                "user1@example.com",
                "2024-02-25",
                "2024-08-14",
                4L
        );

        checkoutRepository.saveAll(Arrays.asList(checkout1, checkout2, checkout3, checkout4));
    }

    @AfterEach
    void tearDown() {
        checkoutRepository.deleteAll();
    }

    @Test
    void findByUserEmailAndBookId() {
        //Given
        Pageable pageable = PageRequest.of(0,10);

        //When
        Checkout found = checkoutRepository.findByUserEmailAndBookId("user1@example.com", 1L);
        Checkout notFound = checkoutRepository.findByUserEmailAndBookId("noexist@example.com", 99L);

        //Then
        assertNotNull(found);
        assertEquals("user1@example.com", found.getUserEmail());
        assertEquals(1L,found.getBookId());
        assertNull(notFound);
    }

    @Test
    void findBooksByUserEmail() {
        // When
        List<Checkout> user1Checkouts = checkoutRepository.findBooksByUserEmail("user1@example.com");
        List<Checkout> user2Checkouts = checkoutRepository.findBooksByUserEmail("user2@example.com");
        List<Checkout> nonExistentUserCheckouts = checkoutRepository.findBooksByUserEmail("nonexistent@example.com");

        // Then
        assertEquals(1, user1Checkouts.size());
        assertTrue(user1Checkouts.stream()
                .allMatch(checkout -> checkout.getUserEmail().equals("user1@example.com")));

        assertEquals(1, user2Checkouts.size());
        assertEquals("user2@example.com", user2Checkouts.get(0).getUserEmail());

        assertTrue(nonExistentUserCheckouts.isEmpty());
    }

    @Test
    void deleteAllByBookId() {
        //When
        checkoutRepository.deleteAllByBookId(1L);
        List<Checkout> remainingCheckouts = checkoutRepository.findAll();

        //Then
        assertEquals(2, remainingCheckouts.size());
        assertTrue(remainingCheckouts.stream()
               .allMatch(checkout -> checkout.getBookId()!= 1L));
    }

    @Test
    void testCheckoutCreation(){
        //Given
        Checkout newCheckout = new Checkout(

                "newuser@example.com",
                "2024-04-25",
                "2024-06-14",
                4L
        );

        //When
        Checkout savedCheckout = checkoutRepository.save(newCheckout);

        //Then
        assertNotNull(savedCheckout.getId());
        assertEquals("newuser@example.com", savedCheckout.getUserEmail());
        assertEquals(4L, savedCheckout.getBookId());
        assertEquals("2024-04-25", savedCheckout.getCheckoutDate());
        assertEquals("2024-06-14", savedCheckout.getReturnDate());
    }

    @Test
    void testMultipleCheckoutsByUser(){
        //Given
        String userEmail = "user1@example.com";

        //When
        List<Checkout> userCheckouts = checkoutRepository.findBooksByUserEmail(userEmail);

        //Then
        assertEquals(2,userCheckouts.size());
        assertTrue(userCheckouts.stream()
                .allMatch(checkout -> checkout.getUserEmail().equals(userEmail)));
        assertTrue(userCheckouts.stream()
                .map(Checkout::getBookId)
                .collect(Collectors.toSet())
                .containsAll(Arrays.asList(1L, 4L)));

    }

    @Test
    void testCheckoutDates() {
        // Given
        Checkout checkout = checkoutRepository.findByUserEmailAndBookId(
                "user2@example.com", 2L);

        // Then
        assertNotNull(checkout);
        assertEquals("2024-02-15", checkout.getCheckoutDate());
        assertEquals("2024-04-04", checkout.getReturnDate());
    }

    @Test
    void testDeleteCheckout() {
        // Given
        Long checkoutId = checkout1.getId();

        // When
        checkoutRepository.deleteById(checkoutId);
        Optional<Checkout> deletedCheckout = checkoutRepository.findById(checkoutId);

        // Then
        assertFalse(deletedCheckout.isPresent());
    }

}