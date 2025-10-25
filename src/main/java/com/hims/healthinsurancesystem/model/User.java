package com.hims.healthinsurancesystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can contain only letters, numbers, and underscores")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @Pattern(regexp = "^\\d{10}$", message = "Contact number must be exactly 10 digits")
    @Column(nullable = true, length = 10)
    private String contactNumber;

    @NotBlank(message = "User type is required")
    @Column(nullable = false, length = 50)
    private String userType;

    @Column(nullable = true)
    private Boolean enabled = true;

    // === Constructors ===
    public User() {}

    public User(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // === Getters & Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username != null ? username.trim() : null; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email.trim().toLowerCase() : null; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", userType='" + userType + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
