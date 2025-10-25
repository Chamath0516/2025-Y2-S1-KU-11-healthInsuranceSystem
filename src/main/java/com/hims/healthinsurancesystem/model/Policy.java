package com.hims.healthinsurancesystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String policyNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "insurance_plan_id")
    private InsurancePlan insurancePlan;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "approved_by_agent_id")
    private InsuranceAgent approvedByAgent;

    // Getters and Setters
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public InsurancePlan getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(InsurancePlan insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public InsuranceAgent getApprovedByAgent() {
        return approvedByAgent;
    }

    public void setApprovedByAgent(InsuranceAgent approvedByAgent) {
        this.approvedByAgent = approvedByAgent;
    }
}