package com.group3.BilliardManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * System user for authentication and authorization.
 * Distinct from Employee — a user is an auth identity;
 * an employee is an HR record. One-to-one link is optional
 * to support system accounts without HR records.
 */
@Entity
@Table(name = "users",
    indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /** JWT refresh token — store hash, not plaintext */
    @Column(name = "refresh_token_hash")
    private String refreshTokenHash;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    /** Soft delete flag */
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /** Optional link to HR employee record */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Employee employee;

    public enum UserStatus {
        ACTIVE, INACTIVE, LOCKED, PENDING_VERIFICATION
    }
}
