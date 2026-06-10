package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class POSViewResponse {
    private Long sessionId;
    private String tableName;
    private String sessionStatus;

    private BigDecimal playAmount;
    private Integer playMinutes;

    private List<OrderResponse> orders;

    private BigDecimal totalOrderAmount;
    private BigDecimal grandTotal;
}
