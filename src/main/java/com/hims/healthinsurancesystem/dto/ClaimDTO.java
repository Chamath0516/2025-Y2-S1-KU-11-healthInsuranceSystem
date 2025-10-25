package com.hims.healthinsurancesystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimDTO {
    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    // Added fields that the claim-form.html binds to
    private Long policyId;
    private Long hospitalId;
    private Long providerId;
    private String description;
    private BigDecimal amount;

    // You can add more fields for medical report, payment, etc.
    // Example:
    // @NotNull(message = "Medical report ID is required")
    // private Long medicalReportId;

    // @NotNull(message = "Payment ID is required")
    // private Long paymentId;
}