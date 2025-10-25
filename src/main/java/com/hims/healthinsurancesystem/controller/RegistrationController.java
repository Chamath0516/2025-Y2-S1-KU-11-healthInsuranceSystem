package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.*;
import com.hims.healthinsurancesystem.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HealthcareProviderService healthcareProviderService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private PendingHospitalService pendingHospitalService;

    @Autowired
    private PendingDoctorService pendingDoctorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==============================
    // CUSTOMER REGISTRATION
    // ==============================

    @GetMapping("/register/customer")
    public String registerCustomerForm(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("error", error);
        return "register-customer";
    }

    @PostMapping("/register/customer")
    public String registerCustomer(@ModelAttribute Customer customer, org.springframework.validation.BindingResult bindingResult, Model model) {
        logger.debug("Received customer data: NIC={}, email={}, firstName={}, lastName={}, dob={}, gender={}",
                customer.getNic(), customer.getEmail(), customer.getFirstName(), customer.getLastName(), customer.getDateOfBirth(), customer.getGender());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.info("Validation errors found for customer registration.");
            bindingResult.getAllErrors().forEach(error -> logger.info("Validation error: {}", error.getDefaultMessage()));
            model.addAttribute("customer", customer);
            model.addAttribute("error", "Please correct the errors in the form.");
            return "register-customer";
        }
        
        // Check if NIC exists
        if (customerService.existsByNic(customer.getNic())) {
            logger.info("NIC already exists: " + customer.getNic());
            return "redirect:/register/customer?error=nicTaken";
        }
        
        // Check if email exists
        if (customerService.existsByEmail(customer.getEmail())) {
            logger.info("Email already exists: " + customer.getEmail());
            return "redirect:/register/customer?error=emailTaken";
        }
        
        // Check if generated username exists
        String generatedUsername = "CUST_" + customer.getNic();
        if (usernameExists(generatedUsername)) {
            logger.info("Generated username already exists: " + generatedUsername);
            return "redirect:/register/customer?error=usernameTaken";
        }
        
        try {
            // Generate username from NIC for customers
            customer.setUsername("CUST_" + customer.getNic());
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customer.setRole(Role.CUSTOMER);
            customer.setUserType("Customer"); // Set userType for customers
            Customer savedCustomer = customerService.save(customer);
            logger.info("Customer saved successfully with ID: {}", savedCustomer.getId());
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            logger.error("Error saving customer: {}", e.getMessage(), e);
            model.addAttribute("customer", customer);
            model.addAttribute("error", "Error saving customer: " + e.getMessage());
            return "register-customer";
        }
    }

    // ==============================
    // PROVIDER REGISTRATION
    // ==============================
    @GetMapping("/register/provider")
    public String registerProviderForm(Model model) {
        model.addAttribute("pendingDoctor", new PendingDoctor());
        // provide hospitals that are already approved so registrant can choose
        model.addAttribute("hospitals", hospitalService.findByStatus(com.hims.healthinsurancesystem.model.Hospital.ApprovalStatus.APPROVED));
        return "register-provider";
    }

    @PostMapping("/register/provider")
    public String registerProvider(@ModelAttribute PendingDoctor pendingDoctor, Model model) {
        // basic checks
        if (pendingDoctor.getEmail() == null || pendingDoctor.getEmail().isBlank()) {
            model.addAttribute("error", "Email is required");
            model.addAttribute("pendingDoctor", pendingDoctor);
            model.addAttribute("hospitals", hospitalService.findByStatus(com.hims.healthinsurancesystem.model.Hospital.ApprovalStatus.APPROVED));
            return "register-provider";
        }

        // username/password required
        if (pendingDoctor.getUsername() == null || pendingDoctor.getUsername().isBlank()) {
            model.addAttribute("error", "Username is required");
            model.addAttribute("pendingDoctor", pendingDoctor);
            model.addAttribute("hospitals", hospitalService.findByStatus(com.hims.healthinsurancesystem.model.Hospital.ApprovalStatus.APPROVED));
            return "register-provider";
        }
        if (pendingDoctor.getPassword() == null || pendingDoctor.getPassword().isBlank()) {
            model.addAttribute("error", "Password is required");
            model.addAttribute("pendingDoctor", pendingDoctor);
            model.addAttribute("hospitals", hospitalService.findByStatus(com.hims.healthinsurancesystem.model.Hospital.ApprovalStatus.APPROVED));
            return "register-provider";
        }

        // prevent duplicates across users and pending table
        boolean emailTaken = customerService.existsByEmail(pendingDoctor.getEmail())
                || healthcareProviderService.existsByEmail(pendingDoctor.getEmail())
                || hospitalService.existsByEmail(pendingDoctor.getEmail())
                || pendingHospitalService.existsByEmail(pendingDoctor.getEmail())
                || pendingDoctorService.existsByEmail(pendingDoctor.getEmail());
        if (emailTaken) {
            return "redirect:/register/provider?error=emailTaken";
        }

        // username uniqueness
        String desiredUsername = pendingDoctor.getUsername();
        if (customerService.existsByUsername(desiredUsername)
                || healthcareProviderService.existsByUsername(desiredUsername)
                || hospitalService.existsByUsername(desiredUsername)
                || pendingDoctorService.existsByUsername(desiredUsername)) {
            return "redirect:/register/provider?error=usernameTaken";
        }

        // store raw password in pending record so admin can access credentials if required
        // (will be encoded on approval)
        pendingDoctor.setPassword(pendingDoctor.getPassword().trim());

        // license uniqueness check in pending and existing providers
        if (pendingDoctorService.existsByLicenseNumber(pendingDoctor.getLicenseNumber())
                || healthcareProviderService.existsByLicenseNumber(pendingDoctor.getLicenseNumber())) {
            // Note: findByUsername used temporarily; ideally repository should have existsByLicenseNumber on provider
            return "redirect:/register/provider?error=licenseTaken";
        }

        pendingDoctorService.save(pendingDoctor);
        return "redirect:/register/provider?submitted=true";
    }

    // ==============================
    // HOSPITAL REGISTRATION
    // ==============================
    @GetMapping("/register/hospital")
    public String registerHospitalForm(Model model) {
        model.addAttribute("hospital", new Hospital());
        return "register-hospital";
    }

    @PostMapping("/register/hospital")
    public String registerHospital(@ModelAttribute Hospital hospital, Model model) {
        // Basic server-side checks for required fields from the simplified form
        if (hospital.getEmail() == null || hospital.getEmail().isBlank()) {
            model.addAttribute("error", "Email is required");
            model.addAttribute("hospital", hospital);
            return "register-hospital";
        }

        // Prevent duplicate emails in existing hospitals/users
        if (hospitalService.existsByEmail(hospital.getEmail()) || pendingHospitalService.existsByEmail(hospital.getEmail())) {
            return "redirect:/register/hospital?error=emailTaken";
        }

        // Create a PendingHospital record instead of creating a full User/Hospital now
        PendingHospital p = new PendingHospital();
        p.setName(hospital.getName());
        p.setLocation(hospital.getLocation());
        p.setEmail(hospital.getEmail());
        p.setContactNumber(hospital.getContactNumber());
        pendingHospitalService.save(p);

        return "redirect:/register/hospital?submitted=true";
    }

    // ==============================
    // AJAX USERNAME CHECK
    // ==============================
    @GetMapping("/api/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsernameUnique(@RequestParam String username) {
        boolean taken = usernameExists(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("taken", taken);
        return response;
    }

    // ==============================
    // AJAX NIC CHECK
    // ==============================
    @GetMapping("/api/check-nic")
    @ResponseBody
    public Map<String, Boolean> checkNicUnique(@RequestParam String nic) {
        logger.debug("Checking NIC uniqueness for: {}", nic);
        boolean taken = customerService.existsByNic(nic);
        logger.debug("NIC taken: {}", taken);
        Map<String, Boolean> response = new HashMap<>();
        response.put("taken", taken);
        return response;
    }

    // ==============================
    // HELPER METHOD
    // ==============================
    private boolean usernameExists(String username) {
        return customerService.existsByUsername(username)
                || healthcareProviderService.existsByUsername(username)
                || hospitalService.existsByUsername(username);
    }
}
