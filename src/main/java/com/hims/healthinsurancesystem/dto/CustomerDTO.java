package com.hims.healthinsurancesystem.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    // Add other fields and validations as necessary
}