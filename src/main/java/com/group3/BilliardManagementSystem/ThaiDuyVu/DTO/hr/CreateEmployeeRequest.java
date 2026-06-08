package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateEmployeeRequest {

    private Long userId;

    private String employmentType;

    private Long branchId;

    private BigDecimal baseSalary;

    private String phone;

    private LocalDate dateOfBirth;
}