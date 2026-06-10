package com.group3.BilliardManagementSystem.MinhChau.controller;

import com.group3.BilliardManagementSystem.Entity.PlaySession;
import com.group3.BilliardManagementSystem.MinhChau.service.PlaySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class PlaySessionController {

    private final PlaySessionService service;

    @PostMapping("/open/{tableId}")
    public Long open(@PathVariable Long tableId) {
        return service.openSession(tableId).getId();
    }

    @PostMapping("/{sessionId}/pause")
    public Long pause(@PathVariable Long sessionId) {
        return service.pauseSession(sessionId).getId();
    }

    @PostMapping("/{sessionId}/resume")
    public Long resume(@PathVariable Long sessionId) {
        return service.resumeSession(sessionId).getId();
    }

    @PostMapping("/{sessionId}/end")
    public Long end(@PathVariable Long sessionId) {
        return service.endSession(sessionId).getId();
    }

    @GetMapping("/{sessionId}/playing-seconds")
    public long getPlayingSeconds(@PathVariable Long sessionId) {
        return service.getPlayingSeconds(sessionId);
    }

    @GetMapping("/table/{tableId}/active")
    public ActiveSessionResponse getActiveSessionByTable(@PathVariable Long tableId) {
        PlaySession session = service.getActiveSessionByTable(tableId);

        if (session == null) {
            return null;
        }

        return new ActiveSessionResponse(
                session.getId(),
                session.getStartTime(),
                session.getStatus().name()
        );
    }

    public record ActiveSessionResponse(
            Long id,
            LocalDateTime startTime,
            String status
    ) {}
}