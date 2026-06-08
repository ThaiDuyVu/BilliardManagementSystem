package com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponse {

    private Long id;

    private String employeeCode;

    private String fullName;

    private String phone;

    private String email;

    private LocalDate dateOfBirth;

    private LocalDate hireDate;

    private BigDecimal baseSalary;

    private String branchName;

    private Long branchId;

    private String employmentType;

    private String status;

    private String username;
}