package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.*;
import com.hims.healthinsurancesystem.dto.ClaimDTO;
import com.hims.healthinsurancesystem.model.*;
import com.hims.healthinsurancesystem.repository.PendingClaimRepository;
import com.hims.healthinsurancesystem.repository.PendingRefundRepository;
import com.hims.healthinsurancesystem.repository.PremiumPaymentRepository;
import com.hims.healthinsurancesystem.repository.ToClaimOfficerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CustomerDashboardController {
    @Autowired
    private DependentService dependentService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private InsurancePlanService insurancePlanService;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private ClaimService claimService; // injected claim service

    @Autowired
    private PremiumPaymentRepository premiumPaymentRepository;

    @Autowired
    private PendingRefundRepository pendingRefundRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PendingClaimRepository pendingClaimRepository; // injected repository

    @Autowired
    private ToClaimOfficerRepository toClaimOfficerRepository;

    // Show My Dependents page with list and add functionality
    @GetMapping("/customer/dependents")
    public String showMyDependents(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        // Try to find customer by NIC first, then by username
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }
        
        if (customer != null) {
            List<Dependent> dependents = dependentService.findByCustomer(customer);
            model.addAttribute("dependents", dependents);
            model.addAttribute("customer", customer);
        }
        
        model.addAttribute("dependent", new Dependent());
        return "my-dependents";
    }

    // Handle add dependent form submission
    @PostMapping("/customer/dependents/add")
    public String addDependent(@ModelAttribute Dependent dependent, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        // Try to find customer by NIC first, then by username
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }
        
        if (customer != null) {
            dependent.setCustomer(customer);
            dependentService.save(dependent);
            redirectAttributes.addFlashAttribute("successMessage", "Dependent added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find logged-in customer.");
        }
        return "redirect:/customer/dependents";
    }

    // Delete dependent
    @PostMapping("/customer/dependents/delete/{id}")
    public String deleteDependent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dependentService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dependent deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting dependent: " + e.getMessage());
        }
        return "redirect:/customer/dependents";
    }

    // Legacy route for backward compatibility
    @GetMapping("/customer/add-dependent")
    public String showAddDependentForm(Model model) {
        model.addAttribute("dependent", new Dependent());
        return "add-dependent";
    }

    // Legacy route for backward compatibility
    @PostMapping("/customer/add-dependent")
    public String addDependentLegacy(@ModelAttribute Dependent dependent, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        // Try to find customer by NIC first, then by username
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }
        
        if (customer != null) {
            dependent.setCustomer(customer);
            dependentService.save(dependent);
            redirectAttributes.addFlashAttribute("successMessage", "Dependent added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find logged-in customer.");
        }
        return "redirect:/customer/dashboard";
    }

    // Placeholder pages for dashboard options
    @GetMapping("/customer/insurance-plans")
    public String viewInsurancePlans(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }
        
        model.addAttribute("customer", customer);
        // Load plans for display
        List<InsurancePlan> plans = insurancePlanService.findAll();
        model.addAttribute("plans", plans);
        return "insurance-plans";
    }

    @GetMapping("/customer/submit-claim")
    public String submitClaim(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        model.addAttribute("customer", customer);

        // Add customer display name
        String customerName = customer != null ? (customer.getUsername() != null ? customer.getUsername() : customer.getNic()) : "";
        model.addAttribute("customerName", customerName);

        // Load existing claims for this customer and pass to the template
        if (customer != null) {
            List<Claim> claims = claimService.findByCustomerId(customer.getId());
            // normalize to non-null list to avoid NPEs
            if (claims == null) claims = List.of();
            model.addAttribute("claims", claims);

            // claims needing information (status PENDING_INFORMATION) - adapt to String status
            List<Claim> claimsNeedingInfo = claims.stream()
                    .filter(c -> c.getStatus() != null && "PENDING_INFORMATION".equalsIgnoreCase(c.getStatus()))
                    .collect(Collectors.toList());
            model.addAttribute("claimsNeedingInfo", claimsNeedingInfo);

            // load pending claims submissions (saved to pending_claims table)
            List<PendingClaim> pendingClaims = pendingClaimRepository.findByCustomer_Id(customer.getId());
            model.addAttribute("pendingClaims", pendingClaims != null ? pendingClaims : List.of());

            // Build a map of any ToClaimOfficer submissions keyed by pendingClaimId so template can show doctor status
            Map<Long, ToClaimOfficer> toSubmissions = new HashMap<>();
            if (pendingClaims != null) {
                for (PendingClaim pc : pendingClaims) {
                    List<ToClaimOfficer> list = toClaimOfficerRepository.findByPendingClaimId(pc.getId());
                    if (list != null && !list.isEmpty()) {
                        // take the first/latest submission
                        toSubmissions.put(pc.getId(), list.get(0));
                    }
                }
            }
            model.addAttribute("toSubmissions", toSubmissions);
        } else {
            model.addAttribute("claims", List.of());
            model.addAttribute("claimsNeedingInfo", List.of());
            model.addAttribute("pendingClaims", List.of());
            model.addAttribute("toSubmissions", Map.of());
        }

        return "submit-claim";
    }

    @GetMapping("/customer/payments")
    public String payments(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();
        
        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        model.addAttribute("customer", customer);

        // find policy for customer (prefer active policy)
        if (customer != null) {
            List<Policy> policies = policyService.findByCustomerId(customer.getId());
            if (policies != null && !policies.isEmpty()) {
                Policy selected = null;
                for (Policy pol : policies) {
                    if (pol.getStatus() != null && "ACTIVE".equalsIgnoreCase(pol.getStatus())) {
                        selected = pol; break;
                    }
                }
                if (selected == null) selected = policies.get(0);

                model.addAttribute("policy", selected);
                InsurancePlan plan = selected.getInsurancePlan();
                model.addAttribute("insurancePlan", plan);
            }

            // existing premium payments for customer
            List<PremiumPayment> existingPayments = premiumPaymentRepository.findByCustomer_Id(customer.getId());
            model.addAttribute("existingPayments", existingPayments);
        }

        return "payments";
    }

    @PostMapping("/payments/process")
    public String processPayment(
            @RequestParam("customerId") Long customerId,
            @RequestParam(name = "insurancePlanId", required = false) Long insurancePlanId,
            @RequestParam(name = "premium") String premiumStr,
            @RequestParam("month") String month,
            @RequestParam(name = "cardholderName", required = false) String cardholderName,
            @RequestParam(name = "maskedCardNumber", required = false) String maskedCardNumber,
            @RequestParam(name = "maskedCvv", required = false) String maskedCvv,
            @RequestParam(name = "expiry", required = false) String expiry,
            @RequestParam(name = "duplicate", defaultValue = "false") String duplicate,
            @RequestParam(name = "refundReason", required = false) String refundReason,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Customer customer = customerService.findById(customerId).orElse(null);
            InsurancePlan plan = null;
            if (insurancePlanId != null) plan = insurancePlanService.findById(insurancePlanId).orElse(null);

            BigDecimal premium = BigDecimal.ZERO;
            try { premium = new BigDecimal(premiumStr); } catch (Exception ignored) {}

            PremiumPayment payment = new PremiumPayment();
            payment.setCustomer(customer);
            payment.setInsurancePlan(plan);
            payment.setPremium(premium);
            payment.setMonth(month);
            payment.setCardholderName(cardholderName);
            payment.setMaskedCardNumber(maskedCardNumber);
            payment.setMaskedCvv(maskedCvv);
            payment.setExpiry(expiry);

            // Determine status based on thresholds
            if (premium.compareTo(new BigDecimal("100000")) >= 0) {
                payment.setStatus("PENDING_APPROVAL");
            } else if (premium.compareTo(new BigDecimal("10000")) <= 0) {
                payment.setStatus("APPROVED");
            } else {
                payment.setStatus("PENDING_REVIEW");
            }

            // Server-side duplicate detection (regardless of client flag)
            boolean isDuplicate = false;
            if (customer != null) {
                if (insurancePlanId != null) {
                    List<PremiumPayment> duplicates = premiumPaymentRepository.findByCustomer_IdAndMonthAndInsurancePlan_Id(customerId, month, insurancePlanId);
                    if (duplicates != null && !duplicates.isEmpty()) isDuplicate = true;
                } else {
                    List<PremiumPayment> byCustomer = premiumPaymentRepository.findByCustomer_Id(customerId);
                    if (byCustomer != null) {
                        for (PremiumPayment pp : byCustomer) {
                            if (month.equals(pp.getMonth())) { isDuplicate = true; break; }
                        }
                    }
                }
            }

            // Save payment
            premiumPaymentRepository.save(payment);

            // If duplicate detected either client or server side -> create pending refund
            if (isDuplicate || "true".equalsIgnoreCase(duplicate)) {
                PendingRefund pr = new PendingRefund();
                pr.setCustomer(customer);
                pr.setPremium(premium);
                pr.setMonth(month);
                pr.setReason(refundReason != null ? refundReason : "duplicate payment");
                pendingRefundRepository.save(pr);
                redirectAttributes.addFlashAttribute("infoMessage", "Duplicate detected: refund created and payment recorded for review.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully.");
            }

            return "redirect:/customer/payments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing payment: " + e.getMessage());
            return "redirect:/customer/payments";
        }
    }

    @GetMapping("/customer/support")
    public String customerSupport(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        model.addAttribute("customer", customer);
        return "customer-support";
    }

    @GetMapping("/customer/notifications")
    public String notifications(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        model.addAttribute("customer", customer);
        return "notifications";
    }

    @GetMapping("/customer/apply-policy")
    public String applyPolicyPage(@RequestParam(name = "planId", required = false) Long planId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        if (customer != null) {
            model.addAttribute("customer", customer);
        }

        List<InsurancePlan> plans = insurancePlanService.findAll();
        model.addAttribute("plans", plans);
        model.addAttribute("selectedPlanId", planId);
        return "apply-policy";
    }

    @GetMapping("/customer/my-tickets")
    public String myTickets(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        model.addAttribute("customer", customer);

        if (customer != null) {
            List<Ticket> tickets = ticketService.findByCustomerId(customer.getId());
            model.addAttribute("tickets", tickets);
        } else {
            model.addAttribute("tickets", List.of());
        }

        return "my-support-tickets";
    }

    @GetMapping("/customer/claims/new")
    public String newClaimForm(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        // Expose CSRF token to Thymeleaf to avoid template null errors
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        model.addAttribute("customer", customer);
        // prepare flags expected by the template
        model.addAttribute("isReadOnly", false);
        model.addAttribute("isEditing", false);

        // empty DTO bound to the form
        model.addAttribute("claimDto", new ClaimDTO());
        // no existing claim when creating a new one
        model.addAttribute("claim", null);

        // load policies belonging to this customer so the policy <select> can be filled
        if (customer != null) {
            List<Policy> policies = policyService.findByCustomerId(customer.getId());
            model.addAttribute("policies", policies);
        } else {
            model.addAttribute("policies", List.of());
        }

        return "claim-form";
    }

    @PostMapping("/customer/claims/submit")
    public String submitClaimForm(@ModelAttribute ClaimDTO claimDto,
                                  @RequestParam(name = "document", required = false) MultipartFile document,
                                  RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to determine logged-in customer. Please login again.");
            return "redirect:/customer/submit-claim";
        }

        try {
            PendingClaim pc = new PendingClaim();
            pc.setCustomer(customer);

            if (claimDto.getPolicyId() != null) {
                policyService.findById(claimDto.getPolicyId()).ifPresent(pc::setPolicy);
            }

            pc.setHospitalId(claimDto.getHospitalId());
            pc.setProviderId(claimDto.getProviderId());
            pc.setDescription(claimDto.getDescription());
            pc.setAmount(claimDto.getAmount());
            pc.setStatus("PENDING");
            pc.setSubmittedAt(LocalDateTime.now());

            if (document != null && !document.isEmpty()) {
                pc.setDocumentName(document.getOriginalFilename());
                try {
                    pc.setDocumentData(document.getBytes());
                } catch (IOException ioe) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to read uploaded document: " + ioe.getMessage());
                    return "redirect:/customer/submit-claim";
                }
            }

            pendingClaimRepository.save(pc);
            redirectAttributes.addFlashAttribute("successMessage", "Claim submitted successfully and is pending review.");
            return "redirect:/customer/submit-claim";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting claim: " + e.getMessage());
            return "redirect:/customer/submit-claim";
        }
    }

    @GetMapping("/customer/claims/{id}/view")
    public String viewClaim(@PathVariable Long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        // load claim
        Claim claim = claimService.findById(id).orElse(null);
        if (claim == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Claim not found.");
            return "redirect:/customer/submit-claim";
        }

        // ensure ownership
        if (claim.getCustomer() == null || customer == null || !claim.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to view this claim.");
            return "redirect:/customer/submit-claim";
        }

        // expose CSRF token
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) model.addAttribute("_csrf", csrf);

        model.addAttribute("customer", customer);
        model.addAttribute("claim", claim);
        model.addAttribute("isReadOnly", true);
        model.addAttribute("isEditing", false);

        // prepare claimDto for the form binding (if template expects it)
        ClaimDTO dto = new ClaimDTO();
        dto.setCustomerId(customer.getId());
        dto.setPolicyId(claim.getPolicy() != null ? claim.getPolicy().getId() : null);
        dto.setDescription(claim.getDescription());
        dto.setAmount(claim.getAmount());
        dto.setStatus(claim.getStatus());
        model.addAttribute("claimDto", dto);

        // policies for the customer's select
        if (customer != null) {
            List<Policy> policies = policyService.findByCustomerId(customer.getId());
            model.addAttribute("policies", policies != null ? policies : List.of());
        } else {
            model.addAttribute("policies", List.of());
        }

        return "claim-form";
    }

    @GetMapping("/customer/claims/{id}/edit")
    public String editClaim(@PathVariable Long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginIdentifier = authentication.getName();

        Customer customer = customerService.findByNic(loginIdentifier);
        if (customer == null) {
            customer = customerService.findByUsername(loginIdentifier);
        }

        Claim claim = claimService.findById(id).orElse(null);
        if (claim == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Claim not found.");
            return "redirect:/customer/submit-claim";
        }

        if (claim.getCustomer() == null || customer == null || !claim.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to edit this claim.");
            return "redirect:/customer/submit-claim";
        }

        // expose CSRF token
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) model.addAttribute("_csrf", csrf);

        model.addAttribute("customer", customer);
        model.addAttribute("claim", claim);
        model.addAttribute("isReadOnly", false);
        model.addAttribute("isEditing", true);

        ClaimDTO dto = new ClaimDTO();
        dto.setCustomerId(customer.getId());
        dto.setPolicyId(claim.getPolicy() != null ? claim.getPolicy().getId() : null);
        dto.setDescription(claim.getDescription());
        dto.setAmount(claim.getAmount());
        dto.setStatus(claim.getStatus());
        model.addAttribute("claimDto", dto);

        if (customer != null) {
            List<Policy> policies = policyService.findByCustomerId(customer.getId());
            model.addAttribute("policies", policies != null ? policies : List.of());
        } else {
            model.addAttribute("policies", List.of());
        }

        return "claim-form";
    }

}
