package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.EscalationService;
import com.hims.healthinsurancesystem.service.TicketService;
import com.hims.healthinsurancesystem.model.Escalation;
import com.hims.healthinsurancesystem.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/support")
public class SupportDashboardController {

    private static final Logger log = LoggerFactory.getLogger(SupportDashboardController.class);

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EscalationService escalationService;

    @GetMapping("/dashboard")
    public String supportDashboard(Model model, Principal principal) {
        // Add username to the model so the template can display it. If Principal is null, template will show fallback text.
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "support-dashboard";
    }

    // JSON endpoint used by the dashboard Track Progress UI
    @GetMapping("/progress")
    @ResponseBody
    public Map<String, Object> ticketProgress(Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Ticket> tickets = ticketService.findAll();
            int total = tickets != null ? tickets.size() : 0;
            long openCount = tickets == null ? 0 : tickets.stream().filter(t -> "OPEN".equalsIgnoreCase(t.getStatus())).count();
            long inProgressCount = tickets == null ? 0 : tickets.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
            long escalatedCount = tickets == null ? 0 : tickets.stream().filter(t -> "ESCALATED".equalsIgnoreCase(t.getStatus())).count();
            long closedCount = tickets == null ? 0 : tickets.stream().filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus())).count();

            double openPercent = total == 0 ? 0 : (openCount * 100.0) / total;
            double inProgressPercent = total == 0 ? 0 : (inProgressCount * 100.0) / total;
            double escalatedPercent = total == 0 ? 0 : (escalatedCount * 100.0) / total;
            double closedPercent = total == 0 ? 0 : (closedCount * 100.0) / total;

            result.put("total", total);
            result.put("openCount", openCount);
            result.put("inProgressCount", inProgressCount);
            result.put("escalatedCount", escalatedCount);
            result.put("closedCount", closedCount);
            result.put("openPercent", Math.round(openPercent));
            result.put("inProgressPercent", Math.round(inProgressPercent));
            result.put("escalatedPercent", Math.round(escalatedPercent));
            result.put("closedPercent", Math.round(closedPercent));
            return result;
        } catch (Exception ex) {
            log.error("Failed to compute ticket progress", ex);
            result.put("error", ex.getMessage());
            return result;
        }
    }

    @GetMapping("/tickets")
    public String supportTickets(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        try {
            List<Ticket> tickets = ticketService.findAll();
            model.addAttribute("tickets", tickets != null ? tickets : Collections.emptyList());
            return "support-tickets";
        } catch (Exception ex) {
            // Log the full exception and return a safe fallback page so the user doesn't see a 500
            log.error("Failed to load tickets for support dashboard", ex);
            model.addAttribute("tickets", Collections.emptyList());
            model.addAttribute("errorMessage", "Unable to load tickets: " + ex.getMessage());
            return "support-tickets-simple";
        }
    }

    @GetMapping("/escalations")
    public String supportEscalations(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        try {
            List<Escalation> escalations = escalationService.findAll();
            model.addAttribute("escalations", escalations != null ? escalations : Collections.emptyList());
            return "support-escalations";
        } catch (Exception ex) {
            log.error("Failed to load escalations for support dashboard", ex);
            model.addAttribute("escalations", Collections.emptyList());
            model.addAttribute("errorMessage", "Unable to load escalations: " + ex.getMessage());
            return "support-escalations-simple";
        }
    }

    // Diagnostic: quick plain-text endpoint to verify controller is reachable
    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }

    // Diagnostic: render a minimal template that doesn't use model attributes
    @GetMapping("/tickets-simple")
    public String ticketsSimple() {
        return "support-tickets-simple";
    }
}
