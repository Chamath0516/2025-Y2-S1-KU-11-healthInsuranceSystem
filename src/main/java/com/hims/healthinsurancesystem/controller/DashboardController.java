package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.CustomerService;
import com.hims.healthinsurancesystem.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        // Try to find customer by NIC first, then by username
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }
        
        if (customer != null) {
            model.addAttribute("customer", customer);
        }
        
        return "customer-dashboard";
    }

    @GetMapping("/hospital/dashboard")
    public String hospitalDashboard() {
        return "hospital-dashboard";
    }

    // Removed duplicate providerDashboard() mapping so ProviderDashboardController handles `/provider/dashboard`.
}
