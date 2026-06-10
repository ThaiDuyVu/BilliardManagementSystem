package com.group3.BilliardManagementSystem.MinhChau.controller;

import com.group3.BilliardManagementSystem.MinhChau.Realtime.TableRealtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class RealtimeController {

    private final TableRealtimeService realtimeService;

    @GetMapping("/api/realtime/tables")
    public SseEmitter streamTables() {
        return realtimeService.subscribe();
    }
}