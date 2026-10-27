
package com.hopeBridgeAi.homeBridgeAi_backend.controller;

import com.hopeBridgeAi.homeBridgeAi_backend.dto.LoginRequest;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.LoginResponse;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.SignupRequest;
import com.hopeBridgeAi.homeBridgeAi_backend.dto.SignupResponse;
import com.hopeBridgeAi.homeBridgeAi_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request) {

        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
