package com.group3.BilliardManagementSystem.ThaiDuyVu.Service.hr;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.CreateEmployeeRequest;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.EmployeeResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.hr.UpdateEmployeeRequest;
import com.group3.BilliardManagementSystem.Entity.Branch;
import com.group3.BilliardManagementSystem.Entity.Employee;
import com.group3.BilliardManagementSystem.Entity.ShiftAssignment;
import com.group3.BilliardManagementSystem.Entity.User;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr.EmployeeRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.hr.ShiftAssignmentRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    @Override
    public EmployeeResponse create(CreateEmployeeRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employeeRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("User already has employee profile");
        }

        Employee.EmploymentType employmentType;

        try {
            employmentType = Employee.EmploymentType.valueOf(
                    request.getEmploymentType().toUpperCase()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Invalid employment type: "
                            + request.getEmploymentType()
            );
        }

        String employeeCode =
                "EMP" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();

        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .fullName(user.getFullName())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .hireDate(LocalDate.now())
                .employmentType(employmentType)
                .baseSalary(request.getBaseSalary())
                .branch(branch)
                .user(user)
                .build();

        employeeRepository.save(employee);

        return mapToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAll() {

        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<EmployeeResponse> getByBranch(Long branchId) {

        return employeeRepository.findByBranchId(branchId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EmployeeResponse mapToResponse(Employee employee) {

        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())

                .email(
                        employee.getUser() != null
                                ? employee.getUser().getEmail()
                                : null
                )

                .branchName(
                        employee.getBranch() != null
                                ? employee.getBranch().getName()
                                : null
                )

                .employmentType(
                        employee.getEmploymentType() != null
                                ? employee.getEmploymentType().name()
                                : null
                )

                .username(
                        employee.getUser() != null
                                ? employee.getUser().getUsername()
                                : null
                )
                .status(employee.getStatus() != null ? employee.getStatus().name() : null)
                .build();
    }
    @Override
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getFullName() != null)
            employee.setFullName(request.getFullName());

        if (request.getPhone() != null)
            employee.setPhone(request.getPhone());

        if (request.getDateOfBirth() != null)
            employee.setDateOfBirth(request.getDateOfBirth());

        if (request.getBaseSalary() != null)
            employee.setBaseSalary(request.getBaseSalary());

        if (request.getEmploymentType() != null) {
            employee.setEmploymentType(
                    Employee.EmploymentType.valueOf(
                            request.getEmploymentType().toUpperCase()
                    )
            );
        }

        if (request.getStatus() != null) {
            employee.setStatus(
                    Employee.EmployeeStatus.valueOf(
                            request.getStatus().toUpperCase()
                    )
            );
        }

        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));

            employee.setBranch(branch);
        }

        employeeRepository.save(employee);

        return mapToResponse(employee);
    }
    @Override
    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // đã terminate rồi thì không cần làm lại
        if (employee.getStatus() == Employee.EmployeeStatus.TERMINATED) {
            throw new RuntimeException("Employee already terminated");
        }

        // check shift tương lai (KHÔNG tính cancelled)
        boolean hasFutureShift =
                shiftAssignmentRepository
                        .existsByEmployeeIdAndWorkDateAfterAndStatusNot(
                                id,
                                LocalDate.now(),
                                ShiftAssignment.AssignmentStatus.CANCELLED
                        );

        if (hasFutureShift) {
            throw new RuntimeException(
                    "Cannot delete employee with future shift assignments"
            );
        }

        // soft delete
        employee.setStatus(Employee.EmployeeStatus.TERMINATED);
        employee.setTerminationDate(LocalDate.now());

        employeeRepository.save(employee);
    }
    @Override
    public EmployeeResponse reactivate(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getStatus() != Employee.EmployeeStatus.TERMINATED) {
            throw new RuntimeException("Only terminated employee can be reactivated");
        }

        employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        employee.setTerminationDate(null);

        employeeRepository.save(employee);

        return mapToResponse(employee);
    }
}