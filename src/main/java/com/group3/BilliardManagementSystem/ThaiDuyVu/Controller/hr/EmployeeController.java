package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.hr;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.CreateEmployeeRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.EmployeeResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.UpdateEmployeeRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Service.hr.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public EmployeeResponse create(@RequestBody CreateEmployeeRequest request) {
        return employeeService.create(request);
    }
    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request
    ) {
        return employeeService.update(id, request);
    }
    @PutMapping("/{id}/reactivate")
    public EmployeeResponse reactivate(@PathVariable Long id) {
        return employeeService.reactivate(id);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
    @GetMapping
    public List<EmployeeResponse> getAll() {
        return employeeService.getAll();
    }
    @GetMapping("/branch/{branchId}")
    public List<EmployeeResponse> getByBranch(
            @PathVariable Long branchId
    ) {
        return employeeService.getByBranch(branchId);
    }   
}