package com.group3.BilliardManagementSystem.ThaiDuyVu.Config;

import com.group3.BilliardManagementSystem.ThaiDuyVu.Security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

                        // PUBLIC
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/api/auth/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/menu/**",
                                "/uploads/**",
                                "/favicon.ico"
                        ).permitAll()
                        // ✅ UI (HTML)
                        .requestMatchers(
                                "/",
                                "/api/branches/**",
                                "/users-ui",
                                "/employee-list",
                                "/employee-create",
                                "/shift-schedule",
                                "/shift-management",
                                "/admin-dashboard",
                                "/dashboard",
                                "/api/auth/**",
                                "/css/**",
                                "/js/**",
                                "/assign-shift"
                        ).permitAll()
                        .requestMatchers("/api/users/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/hr/**")
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/shifts/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                        .requestMatchers("/api/branches/**")
                        .hasAuthority("ROLE_ADMIN")
                        // STATIC
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**"
                        ).permitAll()

                        // PRODUCTS PUBLIC
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/inventory/**").permitAll()
                        // UI
                        .requestMatchers(
                                "/",
                                "/tables/dashboard",
                                "/session/**"
                        ).permitAll()
                        .requestMatchers("/admin-dashboard").hasAuthority("ROLE_ADMIN")
                        // =========================
                        // POS CHECKOUT (IMPORTANT)
                        // =========================
                        .requestMatchers(HttpMethod.POST,
                                "/api/pos/order/*/checkout")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_MANAGER")

                        // POS API (ALL OTHER)
                        .requestMatchers("/api/pos/**")
                        .authenticated()

                        // TABLES / SESSIONS
                        .requestMatchers("/api/tables/**", "/api/sessions/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER", "ROLE_STAFF")

                        // ADMIN ONLY
                        .requestMatchers(
                                "/api/tables/create",
                                "/api/tables/update/**",
                                "/api/tables/delete/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}