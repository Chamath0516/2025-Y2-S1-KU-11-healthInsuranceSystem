package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "approved_claims")
public class ApprovedClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pendingClaimId;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Policy policy;

    @Column(length = 2000)
    private String description;

    private BigDecimal amount;

    private String status;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private String approvedBy;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPendingClaimId() { return pendingClaimId; }
    public void setPendingClaimId(Long pendingClaimId) { this.pendingClaimId = pendingClaimId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}

