package com.group3.BilliardManagementSystem.ThaiDuyVu.Config;

import com.group3.BilliardManagementSystem.Entity.Permission;
import com.group3.BilliardManagementSystem.Entity.Role;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.PermissionRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        initializePermissions();
        initializeRoles();
    }

    private void initializePermissions() {

        createPermissionIfNotExists("USER_CREATE", "Create User", "AUTH");
        createPermissionIfNotExists("USER_READ", "Read User", "AUTH");
        createPermissionIfNotExists("USER_UPDATE", "Update User", "AUTH");
        createPermissionIfNotExists("USER_DELETE", "Delete User", "AUTH");

        createPermissionIfNotExists("ROLE_CREATE", "Create Role", "AUTH");
        createPermissionIfNotExists("ROLE_READ", "Read Role", "AUTH");
        createPermissionIfNotExists("ROLE_UPDATE", "Update Role", "AUTH");
        createPermissionIfNotExists("ROLE_DELETE", "Delete Role", "AUTH");

        createPermissionIfNotExists("EMPLOYEE_CREATE", "Create Employee", "HR");
        createPermissionIfNotExists("EMPLOYEE_READ", "Read Employee", "HR");
        createPermissionIfNotExists("EMPLOYEE_UPDATE", "Update Employee", "HR");
        createPermissionIfNotExists("EMPLOYEE_DELETE", "Delete Employee", "HR");

        createPermissionIfNotExists("ATTENDANCE_CHECKIN", "Attendance Check In", "HR");
        createPermissionIfNotExists("ATTENDANCE_CHECKOUT", "Attendance Check Out", "HR");

        createPermissionIfNotExists("SHIFT_ASSIGN", "Assign Shift", "HR");
    }

    private void initializeRoles() {

        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {

            Set<Permission> permissions =
                    new HashSet<>(permissionRepository.findAll());

            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("System Administrator")
                    .permissions(permissions)
                    .build();

            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName("ROLE_MANAGER").isEmpty()) {

            List<String> managerPermissions = List.of(
                    "USER_READ",
                    "EMPLOYEE_CREATE",
                    "EMPLOYEE_READ",
                    "EMPLOYEE_UPDATE",
                    "EMPLOYEE_DELETE",
                    "ATTENDANCE_CHECKIN",
                    "ATTENDANCE_CHECKOUT",
                    "SHIFT_ASSIGN"
            );

            Set<Permission> permissions =
                    permissionRepository.findAll()
                            .stream()
                            .filter(permission ->
                                    managerPermissions.contains(permission.getCode()))
                            .collect(java.util.stream.Collectors.toSet());

            Role managerRole = Role.builder()
                    .name("ROLE_MANAGER")
                    .description("Branch Manager")
                    .permissions(permissions)
                    .build();

            roleRepository.save(managerRole);
        }

        if (roleRepository.findByName("ROLE_STAFF").isEmpty()) {

            List<String> staffPermissions = List.of(
                    "ATTENDANCE_CHECKIN",
                    "ATTENDANCE_CHECKOUT"
            );

            Set<Permission> permissions =
                    permissionRepository.findAll()
                            .stream()
                            .filter(permission ->
                                    staffPermissions.contains(permission.getCode()))
                            .collect(java.util.stream.Collectors.toSet());

            Role staffRole = Role.builder()
                    .name("ROLE_STAFF")
                    .description("Staff")
                    .permissions(permissions)
                    .build();

            roleRepository.save(staffRole);
        }
    }

    private void createPermissionIfNotExists(
            String code,
            String description,
            String module
    ) {

        if (permissionRepository.findByCode(code).isEmpty()) {

            Permission permission = Permission.builder()
                    .code(code)
                    .description(description)
                    .module(module)
                    .build();

            permissionRepository.save(permission);
        }
    }
}