package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MedicalReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Claim claim;

    private String reportDetails;

    @ManyToOne
    private HealthcareProvider uploadedByProvider;

    private LocalDateTime uploadedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    public void setReportDetails(String reportDetails) {
        this.reportDetails = reportDetails;
    }

    public HealthcareProvider getUploadedByProvider() {
        return uploadedByProvider;
    }

    public void setUploadedByProvider(HealthcareProvider uploadedByProvider) {
        this.uploadedByProvider = uploadedByProvider;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}