package com.hopeBridgeAi.homeBridgeAi_backend.dto;

import java.time.LocalDateTime;

public record OrphanageResponse(

        Long id,
        String name,
        String address,
        String city,
        String contactNumber,
        String email,
        String description,
        Integer capacity,
        Integer currentChildren,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}