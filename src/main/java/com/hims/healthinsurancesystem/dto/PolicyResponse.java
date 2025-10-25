package com.hims.healthinsurancesystem.dto;

public class PolicyResponse {
    private Long id;
    private String policyNumber;
    private String status;
    private String planName;
    private String customerName;

    public PolicyResponse() {
    }

    public PolicyResponse(Long id, String policyNumber, String status, String planName, String customerName) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.status = status;
        this.planName = planName;
        this.customerName = customerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
