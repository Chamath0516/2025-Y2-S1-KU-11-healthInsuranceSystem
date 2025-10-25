package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "denied_payments")
public class DeniedPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "insurance_plan_id")
    private InsurancePlan insurancePlan;

    @Column(precision = 19, scale = 2)
    private BigDecimal premium;

    private String month;

    private String reason;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public InsurancePlan getInsurancePlan() { return insurancePlan; }
    public void setInsurancePlan(InsurancePlan insurancePlan) { this.insurancePlan = insurancePlan; }

    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
