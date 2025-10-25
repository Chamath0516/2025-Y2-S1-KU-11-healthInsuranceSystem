package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    private String subject;

    private String description;

    private String status;

    private LocalDateTime createdAt;

    @ManyToOne
    private CustomerSupportOfficer handledByOfficer;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CustomerSupportOfficer getHandledByOfficer() {
        return handledByOfficer;
    }

    public void setHandledByOfficer(CustomerSupportOfficer handledByOfficer) {
        this.handledByOfficer = handledByOfficer;
    }
}