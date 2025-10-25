package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.CustomerService;
import com.hims.healthinsurancesystem.model.Customer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer savedCustomer = customerService.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        customer.setId(id);
        Customer updatedCustomer = customerService.save(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}