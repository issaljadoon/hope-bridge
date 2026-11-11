
package com.hopeBridgeAi.homeBridgeAi_backend.controller;

import com.hopeBridgeAi.homeBridgeAi_backend.entity.Role;
import com.hopeBridgeAi.homeBridgeAi_backend.service.UserAccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserAccountService userAccountService;

    public AdminUserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Map<String, String>> changeRole(
            @PathVariable Long userId,
            @RequestParam Role role) {

        userAccountService.changeRole(userId, role);

        return ResponseEntity.ok(
                Map.of("message", "User role updated successfully")
        );
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<Map<String, String>> changeStatus(
            @PathVariable Long userId,
            @RequestParam boolean enabled) {

        userAccountService.changeEnabledStatus(userId, enabled);

        return ResponseEntity.ok(
                Map.of("message", "User account status updated successfully")
        );
    }
}
