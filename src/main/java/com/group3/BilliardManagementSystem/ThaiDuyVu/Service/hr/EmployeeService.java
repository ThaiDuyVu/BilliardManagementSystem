package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.hr;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.CreateEmployeeRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.EmployeeResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.UpdateEmployeeRequest;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);
    List<EmployeeResponse> getByBranch(Long branchId);
    List<EmployeeResponse> getAll();
    EmployeeResponse update(Long id, UpdateEmployeeRequest request);
    void delete(Long id);
    EmployeeResponse reactivate(Long id);
}