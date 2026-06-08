package com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth;

import com.group3.BilliardManagementSystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByEmployeeIsNull();
    boolean existsByEmail(String email);

}