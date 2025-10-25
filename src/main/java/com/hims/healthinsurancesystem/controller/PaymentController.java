package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.PaymentService;
import com.hims.healthinsurancesystem.model.Payment;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        Payment savedPayment = paymentService.save(payment);
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @Valid @RequestBody Payment payment) {
        if (!paymentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        payment.setId(id);
        Payment updatedPayment = paymentService.save(payment);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (!paymentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        paymentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}