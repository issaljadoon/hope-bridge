
package com.hopeBridgeAi.homeBridgeAi_backend.dto;

public record SignupResponse(
        Long id,
        String fullName,
        String email,
        String role,
        boolean enabled,
        String message
) {
}
