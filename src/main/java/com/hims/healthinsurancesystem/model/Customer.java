package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
public class Customer extends User {

    @PastOrPresent(message = "Date of birth cannot be in the future")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender", nullable = false)
    private String gender;

    @NotBlank(message = "NIC is required")
    @Pattern(regexp = "^\\d{1,12}$", message = "NIC must contain only digits and be between 1-12 digits")
    @Column(name = "nic", nullable = false, unique = true, length = 12)
    private String nic;

    @Valid
    @Embedded
    private Address address = new Address();

    // === Constructors ===
    public Customer() {
        super();
        this.address = new Address();
    }

    // === Getters & Setters ===
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", address=" + address +
                '}';
    }
}
