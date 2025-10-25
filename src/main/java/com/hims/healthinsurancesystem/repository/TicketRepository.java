package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Add custom queries if needed

    // Fetch tickets for a given customer and join fetch relations used by views
    @Query("select t from Ticket t left join fetch t.customer left join fetch t.handledByOfficer where t.customer.id = :customerId")
    List<Ticket> findByCustomerIdWithRelations(@Param("customerId") Long customerId);

    // Fallback method signature (Spring Data will implement if needed)
    List<Ticket> findByCustomer_Id(Long customerId);

    // Fetch all tickets, join-fetch relations to avoid N+1 problems
    @Query("select distinct t from Ticket t left join fetch t.customer left join fetch t.handledByOfficer")
    List<Ticket> findAllWithRelations();
}