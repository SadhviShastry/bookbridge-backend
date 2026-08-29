package com.bookbridge.controller;

import com.bookbridge.dto.*;
import com.bookbridge.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) { return authService.register(req); }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) { return authService.login(req); }

    @PostMapping("/forgot-password")
    public Map<String, Boolean> forgotPassword(@RequestBody Map<String, String> body) {
        authService.forgotPassword(body.get("email"));
        return Map.of("sent", true);
    }
}
