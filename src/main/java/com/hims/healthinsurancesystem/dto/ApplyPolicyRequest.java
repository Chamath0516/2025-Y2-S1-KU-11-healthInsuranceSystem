package com.hims.healthinsurancesystem.dto;

import java.time.LocalDate;

public class ApplyPolicyRequest {
    private Long customerId;
    private Long planId;
    private LocalDate startDate; // optional; defaults to today

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}

