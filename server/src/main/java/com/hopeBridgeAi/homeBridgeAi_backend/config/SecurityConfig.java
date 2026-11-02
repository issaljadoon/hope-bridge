package com.hopeBridgeAi.homeBridgeAi_backend.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. JWT ROLE CONVERTER
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter scopeConverter =
                new JwtGrantedAuthoritiesConverter();

        Set<String> allowedRoles = Set.of(
                "USER",
                "ADMIN",
                "ORPHAN",
                "DONOR",
                "VOLUNTEER"
        );

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Add scope-based authorities, if present
            Collection<GrantedAuthority> scopes =
                    scopeConverter.convert(jwt);

            if (scopes != null) {
                authorities.addAll(scopes);
            }

            // Extract role from JWT
            Object roleClaim = jwt.getClaim("role");

            if (roleClaim instanceof String role
                    && allowedRoles.contains(role)) {

                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );
            }

            return authorities;
        });

        return converter;
    }

    // 3. SECURITY FILTER CHAIN
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                // Disable CSRF for stateless JWT API
                .csrf(csrf -> csrf.disable())

                // Stateless session management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // API authorization rules
                .authorizeHttpRequests(auth -> auth

                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR
                        ).permitAll()

                        // Authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // CREATE ORPHANAGE - ADMIN ONLY
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/orphanages"
                        ).hasRole("ADMIN")

                        // UPDATE ORPHANAGE - ADMIN ONLY
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orphanages/*"
                        ).hasRole("ADMIN")

                        // DELETE ORPHANAGE - ADMIN ONLY
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/orphanages/*"
                        ).hasRole("ADMIN")

                        // GET ORPHANAGES - PUBLIC
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orphanages",
                                "/api/orphanages/*"
                        ).permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Disable default login methods
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // Enable JWT authentication and custom role converter
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}