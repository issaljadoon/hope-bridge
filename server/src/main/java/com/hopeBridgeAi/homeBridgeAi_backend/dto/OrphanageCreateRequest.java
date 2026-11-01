package com.hopeBridgeAi.homeBridgeAi_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrphanageCreateRequest(

        @NotBlank(message = "Orphanage name is required")
        @Size(max = 150, message = "Name cannot exceed 150 characters")
        String name,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @Size(max = 20, message = "Contact number cannot exceed 20 characters")
        String contactNumber,

        @Email(message = "Enter a valid email address")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        String description,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @NotNull(message = "Current children count is required")
        @Min(value = 0, message = "Current children cannot be negative")
        Integer currentChildren

) {
}