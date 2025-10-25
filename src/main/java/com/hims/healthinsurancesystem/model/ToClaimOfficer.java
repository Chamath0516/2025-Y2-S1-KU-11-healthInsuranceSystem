package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "to_claim_officer")
public class ToClaimOfficer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pendingClaimId;

    // Provider details
    private Long providerId;
    private String providerUsername;
    private String providerFirstName;
    private String providerLastName;
    private String providerEmail;
    private String providerContactNumber;

    // Customer details
    private Long customerId;
    private String customerUsername;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerContactNumber;

    @Column(length = 4000)
    private String justification;

    private String reportDocumentName;

    @Lob
    private byte[] reportDocumentData;

    private LocalDateTime submittedAt;

    private String status;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPendingClaimId() { return pendingClaimId; }
    public void setPendingClaimId(Long pendingClaimId) { this.pendingClaimId = pendingClaimId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProviderUsername() { return providerUsername; }
    public void setProviderUsername(String providerUsername) { this.providerUsername = providerUsername; }

    public String getProviderFirstName() { return providerFirstName; }
    public void setProviderFirstName(String providerFirstName) { this.providerFirstName = providerFirstName; }

    public String getProviderLastName() { return providerLastName; }
    public void setProviderLastName(String providerLastName) { this.providerLastName = providerLastName; }

    public String getProviderEmail() { return providerEmail; }
    public void setProviderEmail(String providerEmail) { this.providerEmail = providerEmail; }

    public String getProviderContactNumber() { return providerContactNumber; }
    public void setProviderContactNumber(String providerContactNumber) { this.providerContactNumber = providerContactNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }

    public String getCustomerFirstName() { return customerFirstName; }
    public void setCustomerFirstName(String customerFirstName) { this.customerFirstName = customerFirstName; }

    public String getCustomerLastName() { return customerLastName; }
    public void setCustomerLastName(String customerLastName) { this.customerLastName = customerLastName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerContactNumber() { return customerContactNumber; }
    public void setCustomerContactNumber(String customerContactNumber) { this.customerContactNumber = customerContactNumber; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getReportDocumentName() { return reportDocumentName; }
    public void setReportDocumentName(String reportDocumentName) { this.reportDocumentName = reportDocumentName; }

    public byte[] getReportDocumentData() { return reportDocumentData; }
    public void setReportDocumentData(byte[] reportDocumentData) { this.reportDocumentData = reportDocumentData; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

