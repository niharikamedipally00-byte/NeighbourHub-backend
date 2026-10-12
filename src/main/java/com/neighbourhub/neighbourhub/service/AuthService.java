package com.neighbourhub.neighbourhub.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.neighbourhub.neighbourhub.dto.AuthResponse;
import com.neighbourhub.neighbourhub.dto.LoginRequest;
import com.neighbourhub.neighbourhub.dto.RegisterRequest;
import com.neighbourhub.neighbourhub.entity.AuthToken;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.AuthTokenRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import com.neighbourhub.neighbourhub.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (request.getPhone() != null &&
                !request.getPhone().isBlank() &&
                userRepository.existsByPhone(request.getPhone())) {

            throw new RuntimeException("Phone number already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new AuthResponse(
                "Registration successful",
                null
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        LocalDateTime expiresAt = jwtService
                .extractExpiration(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        AuthToken authToken = AuthToken.builder()
                .user(user)
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        authTokenRepository.save(authToken);

        return new AuthResponse(
                "Login successful",
                token
        );
    }

    public void logout(String token) {

        AuthToken authToken = authTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Token not found")
                );

        authToken.setRevoked(true);

        authTokenRepository.save(authToken);
    }
}