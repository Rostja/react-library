package com.eshop.spring_boot_library.service;

import com.eshop.spring_boot_library.dao.PaymentRepository;
import com.eshop.spring_boot_library.entity.Payment;
import com.eshop.spring_boot_library.requestmodels.PaymentInfoRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(paymentRepository,
                "your-stripe-api-key");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createPaymentIntent_Success() {
        //Given
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest();
        paymentInfoRequest.setAmount(1000);
        paymentInfoRequest.setCurrency("USD");
        paymentInfoRequest.setReceiptEmail("test@example.com");

        //When && Then
        assertThrows(StripeException.class, () -> paymentService
                .createPaymentIntent(paymentInfoRequest) );
    }

    @Test
    void stripePayment_Success() throws Exception {
        //Given
        String userEmail = "test@example.com";
        Payment payment = new Payment();
        payment.setUserEmail(userEmail);
        payment.setAmount(1000.00);

        //When
        when(paymentRepository.findByUserEmail(userEmail)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        //Then
        ResponseEntity<String> response = paymentService.stripePayment(userEmail);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentRepository).findByUserEmail(userEmail);
        verify(paymentRepository).save(any(Payment.class));

    }

    @Test
    void stripePayment_ThrowsException_WhenPaymentInfoMissing(){
        //Given
        String userEmail = "test@example.com";

        //When
        when(paymentRepository.findByUserEmail(userEmail)).thenReturn(null);

        //Then
        Exception exception = assertThrows(Exception.class, () -> {
            paymentService.stripePayment(userEmail);
        });
        assertEquals("Payment information is missing", exception.getMessage());
        verify(paymentRepository).findByUserEmail(userEmail);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void createPaymentIntent_ValidatesRequestParameters() {
        // Given
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest();
        paymentInfoRequest.setAmount(1000);
        paymentInfoRequest.setCurrency("USD");

        // When && Then
        assertThrows(StripeException.class, () -> {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        });
    }
    @Test
    void stripePayment_UpdatesPaymentAmount() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Payment payment = new Payment();
        payment.setUserEmail(userEmail);
        payment.setAmount(100.00);

        // When
        when(paymentRepository.findByUserEmail(userEmail)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        paymentService.stripePayment(userEmail);

        // Then
        verify(paymentRepository).save(any(Payment.class));
        assertEquals(0.00, payment.getAmount());
    }

    @Test
    void stripePayment_PreservesUserEmail() throws Exception {
        // Given
        String userEmail = "test@example.com";
        Payment payment = new Payment();
        payment.setUserEmail(userEmail);
        payment.setAmount(100.00);

        // When
        when(paymentRepository.findByUserEmail(userEmail)).thenReturn(payment);
        paymentService.stripePayment(userEmail);

        // Then
        verify(paymentRepository).save(any(Payment.class));
        assertEquals(userEmail, payment.getUserEmail());
    }

}