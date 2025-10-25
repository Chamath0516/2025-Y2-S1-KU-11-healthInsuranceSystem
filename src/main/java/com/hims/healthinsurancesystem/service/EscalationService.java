package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Escalation;
import com.hims.healthinsurancesystem.repository.EscalationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EscalationService {

    @Autowired
    private EscalationRepository escalationRepository;

    public Escalation save(Escalation escalation) {
        return escalationRepository.save(escalation);
    }

    public Optional<Escalation> findById(Long id) {
        return escalationRepository.findById(id);
    }

    public List<Escalation> findAll() {
        try {
            return escalationRepository.findAllWithRelations();
        } catch (Exception ex) {
            return escalationRepository.findAll();
        }
    }

    public void deleteById(Long id) {
        escalationRepository.deleteById(id);
    }

    // Note: helper to find latest escalation by customer was removed because the UI no longer
    // needs a customer-level escalation fallback for ticket 'Handled By'. If you later want per-ticket
    // escalation lookup, we can add a ticket-linked field and then fetch per-ticket escalations.
}
