package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.CustomerService;
import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CustomerProfileController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerProfileController.class);

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/customer/profile")
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        // Try to find customer by NIC first, then by username
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }
        
        if (customer != null) {
            logger.info("Loading customer profile for: {}", loginIdentifier);
            logger.debug("Customer DOB: {}", customer.getDateOfBirth());
        }
        
        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    @PostMapping("/customer/profile")
    public String updateProfile(@ModelAttribute("customer") Customer updatedCustomer,
                                RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loginIdentifier = authentication.getName();
            
            // Try to find customer by NIC first, then by username
            Customer customer = customerService.findByNic(loginIdentifier);
            if (customer == null) {
                customer = customerService.findByUsername(loginIdentifier);
            }
            
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Could not find customer.");
                return "redirect:/customer/profile";
            }
            
            // Log the update attempt
            logger.info("Updating customer: {}", loginIdentifier);
            logger.debug("New first name: {}", updatedCustomer.getFirstName());
            logger.debug("New email: {}", updatedCustomer.getEmail());

            // Update customer fields
            customer.setFirstName(updatedCustomer.getFirstName());
            customer.setLastName(updatedCustomer.getLastName());
            customer.setEmail(updatedCustomer.getEmail());
            customer.setContactNumber(updatedCustomer.getContactNumber());
            customer.setDateOfBirth(updatedCustomer.getDateOfBirth());
            customer.setGender(updatedCustomer.getGender());
            
            // Handle address update - create new address if null
            if (updatedCustomer.getAddress() != null) {
                customer.setAddress(updatedCustomer.getAddress());
            } else if (customer.getAddress() == null) {
                // Create a new address if none exists
                customer.setAddress(new Address());
            }
            
            Customer savedCustomer = customerService.save(customer);
            logger.info("Customer saved successfully with ID: {}", savedCustomer.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        
        return "redirect:/customer/profile";
    }

    @GetMapping("/customer/change-password")
    public String showChangePasswordForm() {
        return "customer-change-password";
    }

    @PostMapping("/customer/change-password")
    public String changePassword(String oldPassword, String newPassword, String confirmNewPassword, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loginIdentifier = authentication.getName();
            
            logger.info("Changing password for: {}", loginIdentifier);

            // Try to find customer by NIC first, then by username
            Customer customer = customerService.findByNic(loginIdentifier);
            if (customer == null) {
                customer = customerService.findByUsername(loginIdentifier);
            }
            
            if (customer == null) {
                logger.info("Customer not found for: {}", loginIdentifier);
                redirectAttributes.addFlashAttribute("errorMessage", "Could not find customer.");
                return "redirect:/customer/change-password";
            }
            
            // Validate old password
            if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
                logger.info("Old password mismatch for customer: {}", customer.getId());
                redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
                return "redirect:/customer/change-password";
            }
            
            // Validate new password confirmation
            if (!newPassword.equals(confirmNewPassword)) {
                logger.info("Password confirmation mismatch for customer: {}", customer.getId());
                redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
                return "redirect:/customer/change-password";
            }
            
            // Validate new password length
            if (newPassword.length() < 5) {
                redirectAttributes.addFlashAttribute("errorMessage", "New password must be at least 5 characters long.");
                return "redirect:/customer/change-password";
            }
            
            // Update password
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerService.save(customer);
            
            logger.info("Password changed successfully for customer: {}", customer.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
            
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error changing password: " + e.getMessage());
        }
        
        return "redirect:/customer/change-password";
    }
}
