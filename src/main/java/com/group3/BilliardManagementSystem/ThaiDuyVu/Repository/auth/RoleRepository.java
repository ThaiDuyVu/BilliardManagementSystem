package com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth;

import com.group3.BilliardManagementSystem.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}