
package com.hopeBridgeAi.homeBridgeAi_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/test/protected")
    public Map<String, String> protectedEndpoint() {
        return Map.of(
                "message", "JWT authentication successful!"
        );
    }

    @GetMapping("/api/admin/test")
    public Map<String, String> adminEndpoint() {
        return Map.of("message", "Admin authorization successful!");
    }
}
