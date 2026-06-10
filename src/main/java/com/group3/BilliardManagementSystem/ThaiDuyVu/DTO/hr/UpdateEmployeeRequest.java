package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateEmployeeRequest {

    private String fullName;

    private String phone;

    private LocalDate dateOfBirth;

    private BigDecimal baseSalary;

    private String employmentType;

    private String status;

    private Long branchId;
}