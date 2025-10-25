package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_responses")
public class CustomerResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pendingClaimId;

    private Long customerId;

    @Lob
    @Column(length = 4000)
    private String responseText;

    private String responseDocumentName;

    @Lob
    private byte[] responseDocumentData;

    private LocalDateTime submittedAt;

    public CustomerResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPendingClaimId() {
        return pendingClaimId;
    }

    public void setPendingClaimId(Long pendingClaimId) {
        this.pendingClaimId = pendingClaimId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseDocumentName() {
        return responseDocumentName;
    }

    public void setResponseDocumentName(String responseDocumentName) {
        this.responseDocumentName = responseDocumentName;
    }

    public byte[] getResponseDocumentData() {
        return responseDocumentData;
    }

    public void setResponseDocumentData(byte[] responseDocumentData) {
        this.responseDocumentData = responseDocumentData;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
