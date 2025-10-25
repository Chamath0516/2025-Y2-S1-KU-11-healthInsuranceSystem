package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.EscalationService;
import com.hims.healthinsurancesystem.service.TicketService;
import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.Escalation;
import com.hims.healthinsurancesystem.model.Ticket;
import com.hims.healthinsurancesystem.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CustomerRepository customerRepository;

    // Inject EscalationService to persist escalations when status becomes ESCALATED
    @Autowired
    private EscalationService escalationService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
        // Ensure client provided a customer id
        if (ticket.getCustomer() == null || ticket.getCustomer().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Long customerId = ticket.getCustomer().getId();
        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (optCustomer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Attach the managed Customer entity to avoid detached entity issues
        ticket.setCustomer(optCustomer.get());

        // Default fields
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }
        if (ticket.getCreatedAt() == null) {
            ticket.setCreatedAt(LocalDateTime.now());
        }

        Ticket savedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(savedTicket);
    }

    // New: update only status via PATCH
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        if (payload == null || !payload.containsKey("status")) {
            return ResponseEntity.badRequest().build();
        }
        String status = payload.get("status");
        // optional role assignment when creating an escalation
        String assignedToRole = payload.get("assignedToRole");
        Optional<Ticket> opt = ticketService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ticket ticket = opt.get();

        // Keep previous status to avoid creating duplicate escalations
        String previousStatus = ticket.getStatus();

        ticket.setStatus(status);
        Ticket saved = ticketService.save(ticket);

        // If status was changed to ESCALATED (and wasn't already), create an Escalation copy
        try {
            if (status != null && "ESCALATED".equalsIgnoreCase(status)
                    && (previousStatus == null || !"ESCALATED".equalsIgnoreCase(previousStatus))) {

                Escalation esc = new Escalation();
                esc.setCustomer(ticket.getCustomer());

                // Prefer ticket subject/description if present
                try {
                    // Some Ticket implementations include subject/description
                    esc.setSubject(ticket.getSubject() != null ? ticket.getSubject() : "Ticket #" + ticket.getId());
                } catch (Exception ignore) {
                    esc.setSubject("Ticket #" + ticket.getId());
                }
                try {
                    esc.setDescription(ticket.getDescription());
                } catch (Exception ignore) {
                    esc.setDescription(null);
                }

                // record the assigned role (if provided by the UI)
                if (assignedToRole != null && !assignedToRole.isBlank()) {
                    esc.setAssignedToRole(assignedToRole);
                }

                esc.setStatus("OPEN");
                esc.setCreatedAt(LocalDateTime.now());

                // Try to copy the assigned officer if any
                try {
                    esc.setHandledByOfficer(ticket.getHandledByOfficer());
                } catch (Exception ignore) {
                    // fine if not present
                }

                escalationService.save(esc);
            }
        } catch (Exception ex) {
            // Don't fail the status update if escalation creation fails; log optionally
            // (Logging is intentionally omitted here to keep changes minimal)
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.findAll();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @Valid @RequestBody Ticket ticket) {
        if (ticketService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticket.setId(id);
        Ticket updatedTicket = ticketService.save(ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (ticketService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticketService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}