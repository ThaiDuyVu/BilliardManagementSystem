package com.group3.BilliardManagementSystem.ThaiDuyVu.Security;

import com.group3.BilliardManagementSystem.ThaiDuyVu.Util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // ======================================================
        // 1. BYPASS cho UI pages + public endpoints
        // ======================================================
        if (
                path.startsWith("/login") ||
                        path.startsWith("/register") ||
                        path.startsWith("/shift-schedule") ||
                        path.startsWith("/shift-management") ||
                        path.startsWith("/employee-list") ||
                        path.startsWith("/employee-create") ||
                        path.startsWith("/api/branches") ||
                        path.startsWith("/api/shifts") ||
                        path.startsWith("/api/hr/employees") ||
                        path.startsWith("/css/") ||
                        path.startsWith("/js/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // ======================================================
        // 2. Lấy token
        // ======================================================
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // ======================================================
        // 3. Validate token
        // ======================================================
        if (!jwtUtil.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ======================================================
        // 4. Extract claims
        // ======================================================
        Claims claims = jwtUtil.extractAllClaims(token);

        String username = claims.getSubject();

        List<String> roles = claims.get("roles", List.class);

        if (roles == null) {
            roles = Collections.emptyList();
        }

        List<SimpleGrantedAuthority> authorities =
                roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // ======================================================
        // 5. Set authentication
        // ======================================================
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}