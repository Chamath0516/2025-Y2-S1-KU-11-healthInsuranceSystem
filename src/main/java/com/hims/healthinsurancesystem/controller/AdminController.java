package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.*;
import com.hims.healthinsurancesystem.model.*;
import com.hims.healthinsurancesystem.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

     @Autowired
     private HospitalService hospitalService;

     @Autowired
     private UserService userService;

     @Autowired
     private PendingHospitalService pendingHospitalService;

     @Autowired
     private PendingDoctorService pendingDoctorService;

     @Autowired
     private HealthcareProviderService healthcareProviderService;

     @Autowired
     private PasswordEncoder passwordEncoder;

     @GetMapping("/admin/dashboard")
     public String adminDashboard(Model model) {
         List<User> allUsers = userService.findAll();
         // Filter out the admin user from the list (admin shouldn't deactivate themselves)
         List<User> filteredUsers = allUsers.stream()
                 .filter(user -> !"admin".equals(user.getUsername()))
                 .collect(Collectors.toList());
         model.addAttribute("users", filteredUsers);

         // Add pending hospitals for admin approval
         List<PendingHospital> pendingHospitals = pendingHospitalService.findAll();
         model.addAttribute("pendingHospitals", pendingHospitals);

         // Add pending doctors for admin approval
         List<PendingDoctor> pendingDoctors = pendingDoctorService.findAll();
         model.addAttribute("pendingDoctors", pendingDoctors);
         return "admin-dashboard";
     }

     @PostMapping("/admin/toggle-user/{id}")
     public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         userService.findById(id).ifPresentOrElse(user -> {
             // Treat null as enabled (for existing users)
             boolean currentStatus = user.getEnabled() == null || user.getEnabled();
             user.setEnabled(!currentStatus);
             userService.save(user);
             String status = user.getEnabled() ? "activated" : "deactivated";
             redirectAttributes.addFlashAttribute("message", "User " + user.getUsername() + " has been " + status + " successfully!");
         }, () -> redirectAttributes.addFlashAttribute("error", "User not found!"));
         return "redirect:/admin/dashboard";
     }

     @PostMapping("/admin/delete-user/{id}")
     public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         userService.findById(id).ifPresentOrElse(user -> {
             // Prevent admin from deleting themselves
             if ("admin".equals(user.getUsername())) {
                 redirectAttributes.addFlashAttribute("error", "Cannot delete admin user!");
                 return;
             }

             userService.deleteById(id);
             redirectAttributes.addFlashAttribute("message", "User " + user.getUsername() + " has been deleted successfully!");
         }, () -> redirectAttributes.addFlashAttribute("error", "User not found!"));
         return "redirect:/admin/dashboard";
     }

     @GetMapping("/admin/add-user")
     public String showAddUserForm(Model model) {
         model.addAttribute("user", new User());
         // Only allow internal user roles (exclude ADMIN, CUSTOMER, HOSPITAL)
         List<Role> internalRoles = List.of(
             Role.FINANCE_MANAGER,
             Role.CUSTOMER_SUPPORT_OFFICER,
             Role.CLAIM_PROCESSING_OFFICER,
             Role.INSURANCE_AGENT,
             Role.HEALTHCARE_PROVIDER
         );
         model.addAttribute("roles", internalRoles);
         return "add-user";
     }

     @PostMapping("/admin/add-user")
     public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
         try {
             // Check if username already exists
             if (userService.existsByUsername(user.getUsername())) {
                 redirectAttributes.addFlashAttribute("error", "Username already exists!");
                 return "redirect:/admin/add-user";
             }

             // Check if email already exists
             if (userService.existsByEmail(user.getEmail())) {
                 redirectAttributes.addFlashAttribute("error", "Email already exists!");
                 return "redirect:/admin/add-user";
             }

             // Set default values
             user.setPassword(passwordEncoder.encode("123456")); // Default password
             user.setEnabled(true);
             user.setContactNumber(null); // Internal users don't need contact numbers

             userService.save(user);
             redirectAttributes.addFlashAttribute("message", "User " + user.getUsername() + " has been created successfully!");
             return "redirect:/admin/dashboard";
         } catch (Exception e) {
             redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
             return "redirect:/admin/add-user";
         }
     }

     // Keep the old hospital approval methods for backward compatibility
     @PostMapping("/admin/approve/{id}")
     public String approveHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         hospitalService.findById(id).ifPresentOrElse(hospital -> {
             hospital.setStatus(Hospital.ApprovalStatus.APPROVED);
             hospitalService.save(hospital);
             redirectAttributes.addFlashAttribute("message", "Hospital approved successfully!");
         }, () -> redirectAttributes.addFlashAttribute("error", "Hospital not found!"));
         return "redirect:/admin/dashboard";
     }

     @PostMapping("/admin/reject/{id}")
     public String rejectHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         hospitalService.findById(id).ifPresentOrElse(hospital -> {
             hospital.setStatus(Hospital.ApprovalStatus.REJECTED);
             hospitalService.save(hospital);
             redirectAttributes.addFlashAttribute("message", "Hospital rejected successfully!");
         }, () -> redirectAttributes.addFlashAttribute("error", "Hospital not found!"));
         return "redirect:/admin/dashboard";
     }

     // =====================
     // Pending hospitals approval
     // =====================
     @PostMapping("/admin/approve-pending/{id}")
     public String approvePendingHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         pendingHospitalService.findById(id).ifPresentOrElse(pending -> {
             try {
                 // Check email uniqueness again
                 if (userService.findByEmail(pending.getEmail()) != null || hospitalService.existsByEmail(pending.getEmail())) {
                     redirectAttributes.addFlashAttribute("error", "Email already exists. Cannot approve.");
                     return;
                 }

                 Hospital h = new Hospital();
                 h.setName(pending.getName());
                 h.setLocation(pending.getLocation());
                 h.setContactNumber(pending.getContactNumber());
                 h.setEmail(pending.getEmail());

                 // Generate a unique username
                 String base = (pending.getName() != null && !pending.getName().isBlank())
                         ? pending.getName()
                         : pending.getEmail().split("@")[0];
                 base = base.replaceAll("[^A-Za-z0-9]", "_").toUpperCase();
                 if (base.length() > 20) base = base.substring(0, 20);
                 String candidate = "HOSP_" + base;
                 int suffix = 0;
                 while (userService.existsByUsername(candidate) || hospitalService.existsByUsername(candidate)) {
                     suffix++;
                     candidate = "HOSP_" + base + "_" + suffix;
                     if (suffix > 10000) break;
                 }
                 h.setUsername(candidate);

                 // Generate a temporary password
                 String rawPassword = SecurityUtils.generateRandomPassword();
                 h.setPassword(passwordEncoder.encode(rawPassword));

                 h.setFirstName("Hospital");
                 h.setLastName("User");
                 h.setUserType("Hospital");
                 h.setRole(Role.HOSPITAL);
                 h.setStatus(Hospital.ApprovalStatus.APPROVED);

                 hospitalService.save(h);
                 pendingHospitalService.deleteById(id);
                 redirectAttributes.addFlashAttribute("message", "Pending hospital approved and moved to hospitals table.");
             } catch (Exception e) {
                 redirectAttributes.addFlashAttribute("error", "Error approving pending hospital: " + e.getMessage());
             }
         }, () -> redirectAttributes.addFlashAttribute("error", "Pending hospital not found!"));
         return "redirect:/admin/dashboard";
     }

     // =====================
     // Pending doctors approval
     // =====================
     @PostMapping("/admin/approve-pending-doctor/{id}")
     public String approvePendingDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         pendingDoctorService.findById(id).ifPresentOrElse(pending -> {
             try {
                 // Check email uniqueness
                 if (userService.findByEmail(pending.getEmail()) != null || healthcareProviderService.existsByEmail(pending.getEmail())) {
                     redirectAttributes.addFlashAttribute("error", "Email already exists. Cannot approve doctor.");
                     return;
                 }

                 // Check license uniqueness, but ignore the current pending record (so approving itself doesn't conflict)
                 String license = pending.getLicenseNumber() != null ? pending.getLicenseNumber().trim() : null;
                 if (license != null && !license.isBlank()) {
                     // Check existing providers for this license
                     Optional<HealthcareProvider> existingProviderOpt = healthcareProviderService.findByLicenseNumber(license);
                     if (existingProviderOpt.isPresent()) {
                         HealthcareProvider existing = existingProviderOpt.get();
                         String who = existing.getUsername() != null ? existing.getUsername() : "provider id " + existing.getId();
                         redirectAttributes.addFlashAttribute("error", "License number already used by existing provider: " + who + ". Cannot approve doctor.");
                         return;
                     }

                     // Check other pending doctors with same license (exclude current pending id)
                     Optional<PendingDoctor> otherPendingOpt = pendingDoctorService.findByLicenseNumber(license);
                     if (otherPendingOpt.isPresent() && !otherPendingOpt.get().getId().equals(id)) {
                         redirectAttributes.addFlashAttribute("error", "License number already used by pending registration id: " + otherPendingOpt.get().getId() + ". Cannot approve doctor.");
                         return;
                     }
                 }

                 // Also ensure username is still unique
                 String desiredUsername = pending.getUsername();
                 if (desiredUsername == null || desiredUsername.isBlank()) {
                     redirectAttributes.addFlashAttribute("error", "Pending doctor did not provide a username.");
                     return;
                 }
                 if (userService.findByUsername(desiredUsername) != null || healthcareProviderService.existsByUsername(desiredUsername) || pendingDoctorService.existsByUsername(desiredUsername) && pendingDoctorService.findByUsername(desiredUsername).map(pd -> !pd.getId().equals(id)).orElse(false)) {
                     redirectAttributes.addFlashAttribute("error", "Username already exists. Cannot approve doctor.");
                     return;
                 }

                 // Create provider using stored credentials (password already encoded at registration)
                 HealthcareProvider hp = new HealthcareProvider();
                 hp.setFirstName(pending.getFirstName());
                 hp.setLastName(pending.getLastName());
                 hp.setEmail(pending.getEmail());
                 hp.setContactNumber(pending.getContactNumber());
                 hp.setLicenseNumber(pending.getLicenseNumber());
                 hp.setSpecialization(pending.getSpecialization());

                 // Use the username/password provided during registration (password stored raw in pending)
                 hp.setUsername(pending.getUsername());
                 // encode the stored raw password before saving to the users table
                 hp.setPassword(passwordEncoder.encode(pending.getPassword()));
                 hp.setUserType("HealthcareProvider");
                 hp.setRole(Role.HEALTHCARE_PROVIDER);

                 // Assign hospital if the registrant selected one
                 if (pending.getHospitalId() != null) {
                     hospitalService.findById(pending.getHospitalId()).ifPresent(h -> hp.setHospital(h));
                 }

                 healthcareProviderService.save(hp);
                 pendingDoctorService.deleteById(id);
                 redirectAttributes.addFlashAttribute("message", "Pending doctor approved and added to providers.");
             } catch (Exception e) {
                 logger.error("Error approving pending doctor id {}: {}", id, e.getMessage(), e);
                 redirectAttributes.addFlashAttribute("error", "Error approving pending doctor: " + e.getMessage());
             }
         }, () -> redirectAttributes.addFlashAttribute("error", "Pending doctor not found!"));
         return "redirect:/admin/dashboard";
     }

     @PostMapping("/admin/reject-pending-doctor/{id}")
     public String rejectPendingDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         pendingDoctorService.findById(id).ifPresentOrElse(pending -> {
             pendingDoctorService.deleteById(id);
             redirectAttributes.addFlashAttribute("message", "Pending doctor rejected and removed.");
         }, () -> redirectAttributes.addFlashAttribute("error", "Pending doctor not found!"));
         return "redirect:/admin/dashboard";
     }
 }
