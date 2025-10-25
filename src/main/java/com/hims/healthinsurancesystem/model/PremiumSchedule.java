package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class PremiumSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleID;

    @ManyToOne
    @JoinColumn(name = "policyNumber", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status; // "Pending", "Paid", "Overdue"
}