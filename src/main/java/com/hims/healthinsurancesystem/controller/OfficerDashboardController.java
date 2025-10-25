package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.ClaimService;
import com.hims.healthinsurancesystem.model.ApprovedClaim;
import com.hims.healthinsurancesystem.model.Claim;
import com.hims.healthinsurancesystem.model.PendingClaim;
import com.hims.healthinsurancesystem.model.ToClaimOfficer;
import com.hims.healthinsurancesystem.repository.ApprovedClaimRepository;
import com.hims.healthinsurancesystem.repository.PendingClaimRepository;
import com.hims.healthinsurancesystem.repository.ToClaimOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/officer")
public class OfficerDashboardController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ToClaimOfficerRepository toClaimOfficerRepository;

    @Autowired
    private PendingClaimRepository pendingClaimRepository;

    @Autowired
    private ApprovedClaimRepository approvedClaimRepository;

    @GetMapping("/dashboard")
    public String cpoDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String officerName = authentication != null ? authentication.getName() : "";
        model.addAttribute("officerName", officerName);

        List<Claim> all = claimService.findAll();
        // Claims awaiting review: treat SUBMITTED / PENDING_REVIEW / PENDING as review queue
        List<Claim> claimsForReview = all.stream()
                .filter(c -> {
                    String s = c.getStatus() != null ? c.getStatus().toUpperCase() : "";
                    return s.equals("SUBMITTED") || s.equals("PENDING_REVIEW") || s.equals("PENDING");
                })
                .collect(Collectors.toList());

        // Completed claims (processed by officer)
        List<Claim> completedClaims = all.stream()
                .filter(c -> {
                    String s = c.getStatus() != null ? c.getStatus().toUpperCase() : "";
                    return s.equals("APPROVED") || s.equals("REJECTED");
                })
                .collect(Collectors.toList());

        // Load provider submissions (ToClaimOfficer) that were submitted to CPO
        List<ToClaimOfficer> toSubmissions = toClaimOfficerRepository.findAll().stream()
                .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("SUBMITTED_TO_CPO"))
                .collect(Collectors.toList());

        // Load pending claims that were submitted to CPO (PendingClaim.status = SUBMITTED_TO_CPO)
        List<PendingClaim> pendingSubmitted = pendingClaimRepository.findAll().stream()
                .filter(pc -> pc.getStatus() != null && pc.getStatus().equalsIgnoreCase("SUBMITTED_TO_CPO"))
                .collect(Collectors.toList());

        // Load pending claims that need customer info (PendingClaim.status = PENDING_INFORMATION)
        List<PendingClaim> pendingInfo = pendingClaimRepository.findAll().stream()
                .filter(pc -> pc.getStatus() != null && pc.getStatus().equalsIgnoreCase("PENDING_INFORMATION"))
                .collect(Collectors.toList());

        // Load approved claims (new separate list)
        List<ApprovedClaim> approvedClaims = approvedClaimRepository.findAll();

        model.addAttribute("claimsForReview", claimsForReview);
        model.addAttribute("completedClaims", completedClaims);
        model.addAttribute("toSubmissions", toSubmissions);
        model.addAttribute("pendingSubmitted", pendingSubmitted);
        model.addAttribute("pendingInfo", pendingInfo);
        model.addAttribute("approvedClaims", approvedClaims);
        return "cpo-dashboard";
    }

    @GetMapping("/dashboard-")
    public String cpoDashboardWithDash() {
        return "redirect:/officer/dashboard";
    }
}
