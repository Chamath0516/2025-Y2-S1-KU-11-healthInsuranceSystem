package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.User;
import com.hims.healthinsurancesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;  // fixed generic issue

    @Autowired
    private CustomerService customerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== CustomUserDetailsService.loadUserByUsername called with username: " + username + " ===");
        
        // First try to find by username (for non-customer users)
        User user = userRepository.findByUsername(username).orElse(null);
        System.out.println("User found by username: " + (user != null ? user.getUsername() + " with role: " + user.getRole() : "null"));
        
        // If not found by username, try to find customer by NIC
        if (user == null) {
            Customer customer = customerService.findByNic(username);
            if (customer != null) {
                user = customer;
                System.out.println("Customer found by NIC: " + customer.getUsername() + " with role: " + customer.getRole());
            }
        }
        
        if (user == null) {
            System.out.println("No user found for username: " + username);
            throw new UsernameNotFoundException("User not found");
        }

        String authority = "ROLE_" + user.getRole().name();
        System.out.println("Creating UserDetails with authority: " + authority);
        System.out.println("User password hash: " + user.getPassword());
        System.out.println("User enabled status: " + user.getEnabled());

        // Return Spring Security's internal User with role mapping
        // Treat null as enabled (for existing users before the enabled field was added)
        boolean isEnabled = user.getEnabled() == null || user.getEnabled();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())  // already encoded
                .authorities(authority)  // e.g. ROLE_CUSTOMER, ROLE_ADMIN, etc.
                .disabled(!isEnabled)  // disable only if explicitly set to false
                .build();
    }
}
