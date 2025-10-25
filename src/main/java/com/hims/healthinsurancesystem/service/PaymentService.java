package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Payment;
import com.hims.healthinsurancesystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }
}