package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Controller;

import com.group3.BilliardManagementSystem.Entity.PlaySession;
import com.group3.BilliardManagementSystem.MinhChau.service.PlaySessionService;
import com.group3.BilliardManagementSystem.MinhChau.repository.PlaySessionRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pos/session")
@RequiredArgsConstructor
public class POSController {

    private final PlaySessionRepository sessionRepository;
    private final PlaySessionService sessionService;

    // ---------------------------
    // 1. GET SESSION DETAIL
    // ---------------------------
    @GetMapping("/{sessionId}")
    public SessionResponse getSession(@PathVariable Long sessionId) {

        PlaySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return new SessionResponse(
                session.getId(),
                session.getTable().getTableCode(),
                session.getStatus().name(),
                session.getStartTime()
        );
    }

    // ---------------------------
    // 2. GET REALTIME MONEY
    // ---------------------------
    @GetMapping("/{sessionId}/money")
    public MoneyResponse getMoney(@PathVariable Long sessionId) {

        PlaySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        long seconds = sessionService.getPlayingSeconds(sessionId);
        long minutes = (long) Math.ceil(seconds / 60.0);

        BigDecimal rate = session.getTable().getRatePerMinute();
        BigDecimal total = rate.multiply(BigDecimal.valueOf(minutes));

        return new MoneyResponse(minutes, total);
    }

    // DTO nội bộ đơn giản
    public record MoneyResponse(long playMinutes, BigDecimal totalPlayAmount) {}
}