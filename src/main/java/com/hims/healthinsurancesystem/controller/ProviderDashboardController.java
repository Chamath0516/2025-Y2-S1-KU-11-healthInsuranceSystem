package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.model.HealthcareProvider;
import com.hims.healthinsurancesystem.model.PendingClaim;
import com.hims.healthinsurancesystem.model.ToClaimOfficer;
import com.hims.healthinsurancesystem.repository.PendingClaimRepository;
import com.hims.healthinsurancesystem.repository.ToClaimOfficerRepository;
import com.hims.healthinsurancesystem.service.HealthcareProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/provider")
public class ProviderDashboardController {

    @Autowired
    private HealthcareProviderService healthcareProviderService;

    @Autowired
    private PendingClaimRepository pendingClaimRepository;

    @Autowired
    private ToClaimOfficerRepository toClaimOfficerRepository;

    @GetMapping("/dashboard")
    public String providerDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        HealthcareProvider provider = null;
        if (username != null) {
            provider = healthcareProviderService.findByUsername(username);
        }

        if (provider != null && (provider.getEnabled() == null || provider.getEnabled())) {
            String providerName = provider.getUsername();
            model.addAttribute("providerName", providerName);

            // Load all pending claims assigned to this provider
            List<PendingClaim> allAssigned = pendingClaimRepository.findByProviderId(provider.getId());
            if (allAssigned == null) allAssigned = List.of();

            // Classify claims into three categories for the UI
            List<PendingClaim> claimsNeedingInfo = allAssigned.stream()
                    .filter(pc -> pc.getStatus() != null && pc.getStatus().toUpperCase().contains("INFO"))
                    .collect(Collectors.toList());

            List<PendingClaim> pendingClaims = allAssigned.stream()
                    .filter(pc -> {
                        String s = pc.getStatus();
                        if (s == null) return true; // no status -> awaiting provider report
                        s = s.toUpperCase();
                        return s.equals("PENDING") || s.contains("AWAIT") || s.equals("ASSIGNED") || s.equals("AWAITING_REPORT") || s.equals("AWAITING_PROVIDER") || s.equals("RECEIVED");
                    })
                    .collect(Collectors.toList());

            List<PendingClaim> submittedClaims = allAssigned.stream()
                    .filter(pc -> {
                        String s = pc.getStatus();
                        if (s == null) return false;
                        s = s.toUpperCase();
                        // Treat these as already submitted / under review / finalised
                        // Include any status that contains 'SUBMITTED' (e.g., 'SUBMITTED_TO_CPO')
                        return s.contains("SUBMITTED") || s.equals("IN_REVIEW") || s.equals("APPROVED") || s.equals("REJECTED") || s.equals("COMPLETE");
                    })
                    .collect(Collectors.toList());

            model.addAttribute("pendingClaims", pendingClaims);
            model.addAttribute("claimsNeedingInfo", claimsNeedingInfo);
            model.addAttribute("submittedClaims", submittedClaims);

            // Also include any ToClaimOfficer entries that were created by the CPO asking this provider for info
            final Long providerId = provider.getId();
            List<ToClaimOfficer> toSubmissionsNeedingInfo = toClaimOfficerRepository.findAll().stream()
                    .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("PENDING_DOCTOR"))
                    .filter(t -> Objects.equals(t.getProviderId(), providerId))
                    .collect(Collectors.toList());

            model.addAttribute("toSubmissionsNeedingInfo", toSubmissionsNeedingInfo);
        } else {
            // Provider not found or disabled - show empty dashboard with message
            model.addAttribute("providerName", "");
            model.addAttribute("pendingClaims", List.of());
            model.addAttribute("claimsNeedingInfo", List.of());
            model.addAttribute("submittedClaims", List.of());
            model.addAttribute("toSubmissionsNeedingInfo", List.of());
            model.addAttribute("errorMessage", "Provider account not found or not enabled. If you were recently approved, please try logging in again or contact admin.");
        }

        return "provider-dashboard";
    }
}
