package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.ClaimService;
import com.hims.healthinsurancesystem.model.*;
import com.hims.healthinsurancesystem.repository.ApprovedClaimRepository;
import com.hims.healthinsurancesystem.repository.CustomerResponseRepository;
import com.hims.healthinsurancesystem.repository.PendingClaimRepository;
import com.hims.healthinsurancesystem.repository.ToClaimOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CpoController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private PendingClaimRepository pendingClaimRepository;

    @Autowired
    private ToClaimOfficerRepository toClaimOfficerRepository;

    @Autowired
    private ApprovedClaimRepository approvedClaimRepository;

    @Autowired
    private CustomerResponseRepository customerResponseRepository;

    @GetMapping({"/cpo/claims/{id}/review", "/cpo/pending-claims/{id}/review"})
    public String reviewClaim(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String officerName = authentication != null ? authentication.getName() : "";
        model.addAttribute("officerName", officerName);

        // Ensure completedClaims is always present in the model (non-null list)
        List<Claim> allClaims = claimService.findAll();
        List<Claim> completedClaims = allClaims.stream()
                .filter(c -> {
                    String s = c.getStatus() != null ? c.getStatus().toUpperCase() : "";
                    return s.equals("APPROVED") || s.equals("REJECTED");
                })
                .collect(Collectors.toList());
        model.addAttribute("completedClaims", completedClaims);

        Optional<Claim> claimOpt = claimService.findById(id);
        if (claimOpt.isPresent()) {
            Claim claim = claimOpt.get();
            model.addAttribute("claim", claim);
            model.addAttribute("pendingClaim", null);

            // If this Claim was created from a PendingClaim, try to include any ToClaimOfficer submission
            if (claim.getPendingClaimId() != null) {
                Optional<ToClaimOfficer> toOpt = toClaimOfficerRepository.findByPendingClaimId(claim.getPendingClaimId()).stream().findFirst();
                model.addAttribute("toSubmission", toOpt.orElse(null));
            } else {
                model.addAttribute("toSubmission", null);
            }

            return "cpo-claim-review";
        }

        Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
        if (pendingOpt.isPresent()) {
            model.addAttribute("claim", null);
            PendingClaim pending = pendingOpt.get();
            model.addAttribute("pendingClaim", pending);

            // If there is a matching ToClaimOfficer record for this pending claim, include it
            Optional<ToClaimOfficer> toOpt = toClaimOfficerRepository.findByPendingClaimId(id).stream().findFirst();
            model.addAttribute("toSubmission", toOpt.orElse(null));

            // Load any customer responses for this pending claim
            List<CustomerResponse> responses = customerResponseRepository.findByPendingClaimId(pending.getId());
            model.addAttribute("customerResponses", responses);

            return "cpo-claim-review";
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Claim not found.");
        return "redirect:/officer/dashboard";
    }

    // New endpoint: download pending claim document
    @GetMapping("/cpo/pending-claims/{id}/document")
    public ResponseEntity<byte[]> downloadPendingClaimDocument(@PathVariable Long id) {
        Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
        if (pendingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        PendingClaim pc = pendingOpt.get();
        if (pc.getDocumentData() == null || pc.getDocumentName() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", pc.getDocumentName());
        return new ResponseEntity<>(pc.getDocumentData(), headers, HttpStatus.OK);
    }

    // New endpoint: download doctor's uploaded report document stored in ToClaimOfficer
    @GetMapping("/cpo/to-claim-officer/{id}/doctor-document")
    public ResponseEntity<byte[]> downloadDoctorDocument(@PathVariable Long id) {
        Optional<ToClaimOfficer> opt = toClaimOfficerRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        ToClaimOfficer t = opt.get();
        if (t.getReportDocumentData() == null || t.getReportDocumentName() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", t.getReportDocumentName());
        return new ResponseEntity<>(t.getReportDocumentData(), headers, HttpStatus.OK);
    }

    // New endpoint: download customer response document
    @GetMapping("/cpo/customer-response/{id}/document")
    public ResponseEntity<byte[]> downloadCustomerResponseDocument(@PathVariable Long id) {
        Optional<CustomerResponse> opt = customerResponseRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        CustomerResponse cr = opt.get();
        if (cr.getResponseDocumentData() == null || cr.getResponseDocumentName() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", cr.getResponseDocumentName());
        return new ResponseEntity<>(cr.getResponseDocumentData(), headers, HttpStatus.OK);
    }

    // CPO action: Approve pending claim (create final Claim record and ApprovedClaim record)
    @PostMapping("/cpo/pending-claims/{id}/approve")
    public String approvePendingClaim(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
        if (pendingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
            return "redirect:/officer/dashboard";
        }
        PendingClaim pc = pendingOpt.get();

        try {
            // Create regular Claim record (keeps existing behavior)
            Claim claim = new Claim();
            claim.setCustomer(pc.getCustomer());
            claim.setPolicy(pc.getPolicy());
            claim.setDescription(pc.getDescription());
            claim.setAmount(pc.getAmount());
            claim.setStatus("APPROVED");
            claim.setSubmittedAt(pc.getSubmittedAt() != null ? pc.getSubmittedAt() : LocalDateTime.now());
            // Link back to the originating pending claim so we can find doctor submissions later
            claim.setPendingClaimId(pc.getId());

            claimService.save(claim);

            // Also save into approved_claims table
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String approver = authentication != null ? authentication.getName() : null;

            ApprovedClaim approved = new ApprovedClaim();
            approved.setPendingClaimId(pc.getId());
            approved.setCustomer(pc.getCustomer());
            approved.setPolicy(pc.getPolicy());
            approved.setDescription(pc.getDescription());
            approved.setAmount(pc.getAmount());
            approved.setStatus("APPROVED");
            approved.setSubmittedAt(pc.getSubmittedAt() != null ? pc.getSubmittedAt() : LocalDateTime.now());
            approved.setApprovedAt(LocalDateTime.now());
            approved.setApprovedBy(approver);

            approvedClaimRepository.save(approved);

            // mark pending claim as processed
            pc.setStatus("APPROVED");
            pendingClaimRepository.save(pc);

            redirectAttributes.addFlashAttribute("successMessage", "Claim approved and moved to approved_claims.");
            return "redirect:/officer/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving claim: " + e.getMessage());
            return "redirect:/cpo/claims/" + id + "/review";
        }
    }

    // CPO action: Deny pending claim
    @PostMapping("/cpo/pending-claims/{id}/deny")
    public String denyPendingClaim(@PathVariable Long id, @RequestParam(name = "reason", required = false) String reason, RedirectAttributes redirectAttributes) {
        Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
        if (pendingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
            return "redirect:/officer/dashboard";
        }
        PendingClaim pc = pendingOpt.get();

        try {
            // create a Claim record with REJECTED status so it appears in claim history
            Claim claim = new Claim();
            claim.setCustomer(pc.getCustomer());
            claim.setPolicy(pc.getPolicy());
            claim.setDescription(pc.getDescription());
            claim.setAmount(pc.getAmount());
            claim.setStatus("REJECTED");
            claim.setSubmittedAt(LocalDateTime.now());
            claim.setPendingClaimId(pc.getId());
            claimService.save(claim);

            // update pending claim
            pc.setStatus("REJECTED");
            if (reason != null && !reason.isBlank()) pc.setInformationRequestMessage(reason);
            pendingClaimRepository.save(pc);

            redirectAttributes.addFlashAttribute("successMessage", "Claim rejected and recorded.");
            return "redirect:/officer/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting claim: " + e.getMessage());
            return "redirect:/cpo/claims/" + id + "/review";
        }
    }

    // CPO action: Request more info from customer (puts pending claim in PENDING_INFORMATION)
    @PostMapping("/cpo/pending-claims/{id}/request-info")
    public String requestMoreInfo(@PathVariable Long id,
                                  @RequestParam("message") String message,
                                  @RequestParam(name = "routeTo", required = false, defaultValue = "CUSTOMER") String routeTo,
                                  RedirectAttributes redirectAttributes) {
        Optional<PendingClaim> pendingOpt = pendingClaimRepository.findById(id);
        if (pendingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending claim not found.");
            return "redirect:/officer/dashboard";
        }
        PendingClaim pc = pendingOpt.get();
        // Route to doctor or customer depending on the selector
        if ("DOCTOR".equalsIgnoreCase(routeTo)) {
            // Create a ToClaimOfficer record so it appears on provider/doctor dashboard
            ToClaimOfficer t = new ToClaimOfficer();
            t.setPendingClaimId(pc.getId());
            // populate provider info if available
            t.setProviderId(pc.getProviderId());
            // We intentionally avoid loading the provider entity here to keep this method simple.

            // populate customer snapshot
            if (pc.getCustomer() != null) {
                t.setCustomerId(pc.getCustomer().getId());
                t.setCustomerUsername(pc.getCustomer().getUsername());
                t.setCustomerFirstName(pc.getCustomer().getFirstName());
                t.setCustomerLastName(pc.getCustomer().getLastName());
                t.setCustomerEmail(pc.getCustomer().getEmail());
            }

            t.setJustification(message);
            t.setSubmittedAt(LocalDateTime.now());
            t.setStatus("PENDING_DOCTOR");

            toClaimOfficerRepository.save(t);

            // set pending claim to indicate doctor follow-up (so it doesn't appear as customer-needed info)
            pc.setStatus("PENDING_DOCTOR");
             pendingClaimRepository.save(pc);

            redirectAttributes.addFlashAttribute("successMessage", "Requested more information from the doctor.");
            return "redirect:/officer/dashboard";
        } else {
            // Default behaviour: route to customer
            pc.setStatus("PENDING_INFORMATION");
            pc.setInformationRequestMessage(message);
            pendingClaimRepository.save(pc);

            redirectAttributes.addFlashAttribute("successMessage", "Requested more information from customer.");
            return "redirect:/officer/dashboard";
        }
    }
}
