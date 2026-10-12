package com.neighbourhub.neighbourhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neighbourhub.neighbourhub.dto.AuthResponse;
import com.neighbourhub.neighbourhub.dto.LoginRequest;
import com.neighbourhub.neighbourhub.dto.RegisterRequest;
import com.neighbourhub.neighbourhub.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authorizationHeader) {

        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body("Invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);

        authService.logout(token);

        return ResponseEntity.ok("Logout successful");
    }
}