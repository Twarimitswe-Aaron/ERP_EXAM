package com.rwanda.erp.payroll.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateAdminRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;

    @NotNull(message = "Institution ID is required")
    private UUID institutionId;
}
