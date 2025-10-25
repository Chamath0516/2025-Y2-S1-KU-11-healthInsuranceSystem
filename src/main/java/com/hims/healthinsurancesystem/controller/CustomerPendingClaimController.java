package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.CustomerService;
import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.CustomerResponse;
import com.hims.healthinsurancesystem.model.PendingClaim;
import com.hims.healthinsurancesystem.repository.CustomerResponseRepository;
import com.hims.healthinsurancesystem.repository.PendingClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class CustomerPendingClaimController {

    @Autowired
    private PendingClaimRepository pendingClaimRepository;

    @Autowired
    private CustomerResponseRepository customerResponseRepository;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/pending-claims/{id}/respond")
    public String showRespondForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication != null ? authentication.getName() : null;
        Customer customer = null;
        if (login != null) {
            customer = customerService.findByNic(login);
            if (customer == null) customer = customerService.findByUsername(login);
        }
        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login to respond to requests.");
            return "redirect:/login";
        }

        Optional<PendingClaim> pcOpt = pendingClaimRepository.findById(id);
        if (pcOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
            return "redirect:/customer/submit-claim";
        }
        PendingClaim pc = pcOpt.get();
        if (pc.getCustomer() == null || !pc.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to respond to this request.");
            return "redirect:/customer/submit-claim";
        }

        model.addAttribute("pendingClaim", pc);
        return "pending-claim-respond-form";
    }

    @PostMapping("/customer/pending-claims/{id}/respond")
    public String submitResponse(@PathVariable Long id,
                                 @RequestParam(name = "responseText", required = false) String responseText,
                                 @RequestParam(name = "responseDocument", required = false) MultipartFile responseDocument,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication != null ? authentication.getName() : null;
        Customer customer = null;
        if (login != null) {
            customer = customerService.findByNic(login);
            if (customer == null) customer = customerService.findByUsername(login);
        }
        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login to submit a response.");
            return "redirect:/login";
        }

        Optional<PendingClaim> pcOpt = pendingClaimRepository.findById(id);
        if (pcOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
            return "redirect:/customer/submit-claim";
        }
        PendingClaim pc = pcOpt.get();
        if (pc.getCustomer() == null || !pc.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to respond to this request.");
            return "redirect:/customer/submit-claim";
        }

        try {
            String existing = pc.getDescription() != null ? pc.getDescription() : "";
            String appended = existing + "\n\nCustomer response:\n" + (responseText != null ? responseText : "");
            pc.setDescription(appended);

            if (responseDocument != null && !responseDocument.isEmpty()) {
                pc.setDocumentName(responseDocument.getOriginalFilename());
                pc.setDocumentData(responseDocument.getBytes());
            }

            // Clear the information request message and mark as submitted back to CPO
            pc.setInformationRequestMessage(null);
            pc.setStatus("SUBMITTED_TO_CPO");
            pc.setSubmittedAt(LocalDateTime.now());
            pendingClaimRepository.save(pc);

            // Also persist a dedicated CustomerResponse record so CPO can see structured responses
            CustomerResponse cr = new CustomerResponse();
            cr.setPendingClaimId(pc.getId());
            cr.setCustomerId(customer.getId());
            cr.setResponseText(responseText != null ? responseText : "");
            if (responseDocument != null && !responseDocument.isEmpty()) {
                cr.setResponseDocumentName(responseDocument.getOriginalFilename());
                cr.setResponseDocumentData(responseDocument.getBytes());
            }
            cr.setSubmittedAt(LocalDateTime.now());
            customerResponseRepository.save(cr);

            redirectAttributes.addFlashAttribute("successMessage", "Your response has been submitted and will appear on the officer dashboard.");
            return "redirect:/customer/submit-claim";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to read uploaded document: " + e.getMessage());
            return "redirect:/customer/pending-claims/" + id + "/respond";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting response: " + e.getMessage());
            return "redirect:/customer/pending-claims/" + id + "/respond";
        }
    }
}
