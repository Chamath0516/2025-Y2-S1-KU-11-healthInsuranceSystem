package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Dependent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dependentId;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String relationshipToCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Getters and Setters
    public Long getDependentId() {
        return dependentId;
    }
    public void setDependentId(Long dependentId) {
        this.dependentId = dependentId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getRelationshipToCustomer() {
        return relationshipToCustomer;
    }
    public void setRelationshipToCustomer(String relationshipToCustomer) {
        this.relationshipToCustomer = relationshipToCustomer;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

