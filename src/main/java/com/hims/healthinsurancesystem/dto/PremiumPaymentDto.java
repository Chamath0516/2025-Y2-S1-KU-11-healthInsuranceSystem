package com.hims.healthinsurancesystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PremiumPaymentDto {
    private Long id;
    private String customer;
    private String plan;
    private BigDecimal premium;
    private String month;
    private String status;
    private LocalDateTime createdAt;

    public PremiumPaymentDto() {}

    public PremiumPaymentDto(Long id, String customer, String plan, BigDecimal premium, String month, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.plan = plan;
        this.premium = premium;
        this.month = month;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

