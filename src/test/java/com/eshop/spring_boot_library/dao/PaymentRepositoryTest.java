package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.Payment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment1;
    private Payment payment2;
    private Payment payment3;

    @BeforeEach
    void setUp() {
        payment1 = new Payment();
        payment1.setUserEmail("user1@example.com");
        payment1.setAmount(100.50);

        payment2 = new Payment();
        payment2.setUserEmail("user2@example.com");
        payment2.setAmount(200.75);

        payment3 = new Payment();
        payment3.setUserEmail("user3@example.com");
        payment3.setAmount(550.20);

        paymentRepository.saveAll(List.of(payment1, payment2, payment3));
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    void findByUserEmail() {
        //When
        Payment foundPayment = paymentRepository.findByUserEmail("user1@example.com");

        //Then
        assertNotNull(foundPayment);
        assertEquals("user1@example.com", foundPayment.getUserEmail());
        assertEquals(100.50, foundPayment.getAmount());
    }

    @Test
    void findByUserEmail_NonExistingUser_ShouldReturnNull() {
        // When
        Payment foundPayment = paymentRepository.findByUserEmail("nonesistentuser1@example.com");

        // Then
        assertNull(foundPayment);

    }

    @Test
    void createPayment_ShouldSaveAndGenerateId() {
        // Given
        Payment newPayment = new Payment();
        newPayment.setUserEmail("user3@example.com");
        newPayment.setAmount(300.00);

        // When
        Payment savedPayment = paymentRepository.save(newPayment);

        // Then
        assertNotNull(savedPayment.getId());
        assertEquals("user3@example.com", savedPayment.getUserEmail());
        assertEquals(300.00, savedPayment.getAmount());
    }

    @Test
    void updatePayment_ShouldModifyExistingPayment(){
        //Given
        Payment paymentToUpdate = paymentRepository.findByUserEmail("user1@example.com");
        double newAmount = 150.70;

        //When
        paymentToUpdate.setAmount(newAmount);
        Payment updatedPayment = paymentRepository.save(paymentToUpdate);

        //Then
        assertEquals(newAmount, updatedPayment.getAmount());
    }

    @Test
    void deletePayment_ShouldRemovePayment() {
        // Given
        Long paymentId = payment1.getId();

        // When
        paymentRepository.deleteById(paymentId);
        Optional<Payment> deletedPayment = paymentRepository.findById(paymentId);

        // Then
        assertFalse(deletedPayment.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllPayments() {
        // When
        List<Payment> allPayments = paymentRepository.findAll();

        // Then
        assertEquals(3, allPayments.size());
        assertTrue(allPayments.stream()
                .anyMatch(p -> p.getUserEmail().equals("user1@example.com")));
        assertTrue(allPayments.stream()
                .anyMatch(p -> p.getUserEmail().equals("user2@example.com")));
    }

    @Test
    void savePayment_WithNegativeAmount_ShouldWork() {
        // Given
        Payment negativePayment = new Payment();
        negativePayment.setUserEmail("negative@example.com");
        negativePayment.setAmount(-50.00);

        // When
        Payment savedPayment = paymentRepository.save(negativePayment);

        // Then
        assertNotNull(savedPayment.getId());
        assertEquals(-50.00, savedPayment.getAmount());
    }

    @Test
    void savePayment_WithZeroAmount_ShouldWork() {
        // Given
        Payment zeroPayment = new Payment();
        zeroPayment.setUserEmail("zero@example.com");
        zeroPayment.setAmount(0.00);

        // When
        Payment savedPayment = paymentRepository.save(zeroPayment);

        // Then
        assertNotNull(savedPayment.getId());
        assertEquals(0.00, savedPayment.getAmount());
    }

}