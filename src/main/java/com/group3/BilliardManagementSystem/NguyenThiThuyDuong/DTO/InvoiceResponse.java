package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponse(

        Long id,
        String invoiceNumber,

        Long sessionId,
        Long userId,

        LocalDateTime issuedAt,

        Integer playMinutes,
        BigDecimal ratePerMinute,

        BigDecimal playAmount,
        BigDecimal serviceAmount,
        BigDecimal totalAmount,

        String status

) {}