package com.hims.healthinsurancesystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PendingRefundDto {
    private Long id;
    private String customer;
    private BigDecimal amount;
    private String month;
    private String reason;
    private String status;
    private LocalDateTime createdAt;

    public PendingRefundDto() {}

    public PendingRefundDto(Long id, String customer, BigDecimal amount, String month, String reason, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.month = month;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

