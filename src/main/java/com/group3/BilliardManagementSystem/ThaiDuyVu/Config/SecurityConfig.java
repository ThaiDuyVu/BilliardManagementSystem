package com.group3.BilliardManagementSystem.ThaiDuyVu.Config;

import com.group3.BilliardManagementSystem.ThaiDuyVu.Security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC API
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ UI (HTML)
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/api/branches/**",
                                "/users-ui",
                                "/employee-list",
                                "/employee-create",
                                "/shift-schedule",
                                "/shift-management",
                                "/inventory/**",
                                "/api/shifts/**",
                                "/api/auth/**",
                                "/api/shifts/**",
                                "/api/branches/**",
                                "/api/hr/employees/**",
                                "/css/**",
                                "/js/**",
                                "/assign-shift"
                        ).permitAll()

                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}