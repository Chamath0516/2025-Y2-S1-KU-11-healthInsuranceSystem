package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.HealthcareProviderService;
import com.hims.healthinsurancesystem.model.HealthcareProvider;
import com.hims.healthinsurancesystem.model.PendingClaim;
import com.hims.healthinsurancesystem.model.ToClaimOfficer;
import com.hims.healthinsurancesystem.repository.PendingClaimRepository;
import com.hims.healthinsurancesystem.repository.ToClaimOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/provider/claims")
public class ProviderClaimsController {

    @Autowired
    private PendingClaimRepository pendingClaimRepository;

    @Autowired
    private ToClaimOfficerRepository toClaimOfficerRepository;

    @Autowired
    private HealthcareProviderService healthcareProviderService;

    // Show report form - if provider already submitted, include existing submission for editing
    @GetMapping("/{id}/report")
    public String showReportForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
            if (pendingOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
                return "redirect:/provider/dashboard";
            }
            PendingClaim pending = pendingOpt.get();

            // Authenticate provider
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            if (username == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to submit or edit a report.");
                return "redirect:/login";
            }

            HealthcareProvider provider = healthcareProviderService.findByUsername(username);
            if (provider == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Provider account not found.");
                return "redirect:/provider/dashboard";
            }

            // Ensure provider is assigned to this pending claim (if the claim has an assigned provider)
            if (pending.getProviderId() != null && !pending.getProviderId().equals(provider.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not assigned to this pending claim.");
                return "redirect:/provider/dashboard";
            }

            model.addAttribute("pendingClaim", pending);

            // Find an existing submission by this provider for this pending claim (if any)
            List<ToClaimOfficer> existing = toClaimOfficerRepository.findByPendingClaimId(id);
            Optional<ToClaimOfficer> mySubmission = existing.stream().filter(t -> t.getProviderId() != null && t.getProviderId().equals(provider.getId())).findFirst();
            model.addAttribute("submission", mySubmission.orElse(null));

            return "provider-report-form";
        } catch (Exception ex) {
            // Log and redirect with a friendly message instead of allowing a blank/white error page
            System.err.println("Error in showReportForm: " + ex.getMessage());
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while opening the report form. Please try again.");
            return "redirect:/provider/dashboard";
        }
    }

    // Submit or update provider report
    @PostMapping("/{id}/report")
    public String submitReport(@PathVariable("id") Long id,
                               @RequestParam("justification") String justification,
                               @RequestParam(name = "file", required = false) MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            // Authenticate provider
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            if (username == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to submit a report.");
                return "redirect:/login";
            }

            HealthcareProvider provider = healthcareProviderService.findByUsername(username);
            if (provider == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Provider account not found.");
                return "redirect:/provider/dashboard";
            }

            Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
            if (pendingOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
                return "redirect:/provider/dashboard";
            }

            PendingClaim pending = pendingOpt.get();

            // Ensure the provider is allowed to submit for this pending claim
            if (pending.getProviderId() != null && !pending.getProviderId().equals(provider.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not assigned to this pending claim.");
                return "redirect:/provider/dashboard";
            }

            // Try to find an existing submission from this provider for the pending claim; if present, update it, otherwise create new
            List<ToClaimOfficer> existing = toClaimOfficerRepository.findByPendingClaimId(id);
            Optional<ToClaimOfficer> maybeExisting = existing.stream().filter(t -> t.getProviderId() != null && t.getProviderId().equals(provider.getId())).findFirst();

            ToClaimOfficer dto = maybeExisting.orElseGet(ToClaimOfficer::new);
            if (dto.getId() == null) {
                dto.setPendingClaimId(pending.getId());
                dto.setProviderId(provider.getId());
                dto.setProviderUsername(provider.getUsername());
                dto.setProviderFirstName(provider.getFirstName());
                dto.setProviderLastName(provider.getLastName());
                dto.setProviderEmail(provider.getEmail());
                dto.setProviderContactNumber(provider.getContactNumber());
            }

            // customer info if present (update in case customer data changed)
            if (pending.getCustomer() != null) {
                var cust = pending.getCustomer();
                dto.setCustomerId(cust.getId());
                dto.setCustomerUsername(cust.getUsername());
                dto.setCustomerFirstName(cust.getFirstName());
                dto.setCustomerLastName(cust.getLastName());
                dto.setCustomerEmail(cust.getEmail());
                dto.setCustomerContactNumber(cust.getContactNumber());
            }

            dto.setJustification(justification);
            dto.setSubmittedAt(LocalDateTime.now());
            dto.setStatus("SUBMITTED_TO_CPO");

            if (file != null && !file.isEmpty()) {
                try {
                    dto.setReportDocumentName(file.getOriginalFilename());
                    dto.setReportDocumentData(file.getBytes());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to read uploaded file: " + e.getMessage());
                    return "redirect:/provider/claims/" + id + "/report";
                }
            }

            toClaimOfficerRepository.save(dto);

            // Update pending claim status and clear information request message
            pending.setStatus("SUBMITTED_TO_CPO");
            pending.setInformationRequestMessage(null);
            pendingClaimRepository.save(pending);

            redirectAttributes.addFlashAttribute("successMessage", "Report uploaded and sent to Claim Processing Officer.");
            // Redirect back to the report form so the provider can review or further edit the submission
            return "redirect:/provider/claims/" + id + "/report";
        } catch (Exception ex) {
            System.err.println("Error in submitReport: " + ex.getMessage());
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while saving the report. Please try again.");
            return "redirect:/provider/dashboard";
        }
    }

    // Simple view for existing ToClaimOfficer submissions (optional)
    @GetMapping("/{id}/view-submission")
    public String viewSubmission(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ToClaimOfficer> opt = toClaimOfficerRepository.findByPendingClaimId(id).stream().findFirst();
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No submission found for this pending claim.");
            return "redirect:/provider/dashboard";
        }
        model.addAttribute("submission", opt.get());
        return "provider-submission-view"; // optional template
    }

    // Read-only view for a pending claim (provider view)
    @GetMapping("/{id}/view")
    public String viewPendingClaim(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
        if (pendingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
            return "redirect:/provider/dashboard";
        }
        PendingClaim pending = pendingOpt.get();
        model.addAttribute("pendingClaim", pending);

        // If there is any ToClaimOfficer submission related to this pending claim, include the first one
        Optional<ToClaimOfficer> toOpt = toClaimOfficerRepository.findByPendingClaimId(id).stream().findFirst();
        model.addAttribute("submission", toOpt.orElse(null));

        return "provider-claim-view";
    }
}
