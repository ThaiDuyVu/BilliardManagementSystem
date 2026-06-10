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
        // 1. BYPASS PUBLIC ENDPOINTS (IMPORTANT FIX)
        // ======================================================
        if (
                path.startsWith("/login") ||
                        path.startsWith("/register") ||
                        path.startsWith("/api/auth") ||
                        path.startsWith("/api/products") ||
                        path.startsWith("/css/") ||
                        path.startsWith("/js/") ||
                        path.startsWith("/images/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // ======================================================
        // 2. BYPASS POS SYSTEM (DEV MODE FIX FOR YOUR 403 ISSUE)
        // ======================================================


        // ======================================================
        // 3. GET TOKEN
        // ======================================================
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("PATH: " + path);
            System.out.println("AUTH HEADER: " + authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // ======================================================
        // 4. VALIDATE TOKEN
        // ======================================================
        try {
            if (!jwtUtil.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // ======================================================
            // 5. EXTRACT CLAIMS
            // ======================================================
            Claims claims = jwtUtil.extractAllClaims(token);

            String username = claims.getSubject();

            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                roles = Collections.emptyList();
            }

            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            // ======================================================
            // 6. SET SECURITY CONTEXT
            // ======================================================
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // IMPORTANT: never block whole request due to JWT parse error
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}