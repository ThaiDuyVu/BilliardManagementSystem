package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        String tableCode,
        String status,
        LocalDateTime startTime
) {}