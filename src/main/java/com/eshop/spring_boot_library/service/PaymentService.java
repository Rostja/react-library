package com.eshop.spring_boot_library.service;

import com.eshop.spring_boot_library.dao.PaymentRepository;
import com.stripe.Stripe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        // Initialize Stripe API with secret key
        Stripe.apiKey = secretKey;
    }
}
