package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {

    // Explicit JPQL to fetch relations (customer and handledByOfficer) to avoid query creation errors
    @Query("SELECT DISTINCT e FROM Escalation e LEFT JOIN FETCH e.customer LEFT JOIN FETCH e.handledByOfficer")
    List<Escalation> findAllWithRelations();
}
