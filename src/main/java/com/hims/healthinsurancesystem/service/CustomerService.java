package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Save or update a customer.
     * Transactional ensures atomic write.
     */
    @Transactional
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    /** Find a customer by ID. */
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    /** Return all customers. */
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /** Delete customer by ID. */
    @Transactional
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    /** Check if a username already exists (trim + case-insensitive). */
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return customerRepository.existsByUsername(username.trim());
    }

    /** Check if an email already exists. */
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return customerRepository.existsByEmail(email.trim());
    }

    /** Find customer by username (used in login & dashboards). */
    public Customer findByUsername(String username) {
        if (username == null) return null;
        return customerRepository.findByUsername(username.trim())
                .orElse(null);
    }

    /** Find customer by NIC (used in login & dashboards). */
    public Customer findByNic(String nic) {
        if (nic == null) return null;
        return customerRepository.findByNic(nic.trim())
                .orElse(null);
    }

    /** Check if NIC already exists. */
    public boolean existsByNic(String nic) {
        if (nic == null) return false;
        return customerRepository.existsByNic(nic.trim());
    }

    /** Validate login credentials (for internal use if needed). */
    public Optional<Customer> validateLogin(String username, String encodedPassword) {
        Customer customer = findByUsername(username);
        if (customer != null && customer.getPassword().equals(encodedPassword)) {
            return Optional.of(customer);
        }
        return Optional.empty();
    }
}
