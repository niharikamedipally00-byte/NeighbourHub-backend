package com.neighbourhub.neighbourhub.security;

import com.neighbourhub.neighbourhub.entity.AuthToken;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.AuthTokenRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            AuthTokenRepository authTokenRepository,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.authTokenRepository = authTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            AuthToken authToken =
                    authTokenRepository
                            .findByToken(token)
                            .orElse(null);

            if (authToken == null ||
                    authToken.isRevoked()) {

                filterChain.doFilter(request, response);
                return;
            }

            String email =
                    jwtService.extractEmail(token);

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);

            if (user != null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" +
                                                user.getRole().name()
                                        )
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(request, response);
    }
}