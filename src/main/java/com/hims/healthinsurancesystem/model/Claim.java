package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Policy policy;

    private String description;

    private BigDecimal amount;

    private String status;

    private LocalDateTime submittedAt;

    @ManyToOne
    private ClaimProcessingOfficer reviewedByOfficer;

    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;

    // Added provider relationship so Thymeleaf can access claim.provider
    @ManyToOne
    private HealthcareProvider provider;

    // --- New field: link back to originating pending claim (nullable) ---
    private Long pendingClaimId;

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

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ClaimProcessingOfficer getReviewedByOfficer() {
        return reviewedByOfficer;
    }

    public void setReviewedByOfficer(ClaimProcessingOfficer reviewedByOfficer) {
        this.reviewedByOfficer = reviewedByOfficer;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public HealthcareProvider getProvider() {
        return provider;
    }

    public void setProvider(HealthcareProvider provider) {
        this.provider = provider;
    }

    public Long getPendingClaimId() {
        return pendingClaimId;
    }

    public void setPendingClaimId(Long pendingClaimId) {
        this.pendingClaimId = pendingClaimId;
    }
}