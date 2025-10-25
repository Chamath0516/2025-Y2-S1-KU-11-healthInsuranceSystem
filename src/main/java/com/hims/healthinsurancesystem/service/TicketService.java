package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Ticket;
import com.hims.healthinsurancesystem.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findAll() {
        // Use join-fetch query to load relations needed by the views
        try {
            return ticketRepository.findAllWithRelations();
        } catch (Exception ex) {
            // Fallback to default findAll if custom query fails for any reason
            return ticketRepository.findAll();
        }
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    // New: find tickets belonging to a specific customer (with relations)
    public List<Ticket> findByCustomerId(Long customerId) {
        if (customerId == null) return List.of();
        try {
            return ticketRepository.findByCustomerIdWithRelations(customerId);
        } catch (Exception ex) {
            // fallback to repository-derived method
            return ticketRepository.findByCustomer_Id(customerId);
        }
    }
}