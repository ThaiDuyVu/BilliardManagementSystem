package com.group3.BilliardManagementSystem.Entity;

import com.group3.BilliardManagementSystem.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines a named role (e.g., ADMIN, CASHIER, STAFF) with a set of permissions.
 * Users are assigned roles; roles grant permissions.
 * This two-level RBAC model supports fine-grained access control.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name; // e.g., "ROLE_ADMIN", "ROLE_CASHIER"

    @Column(name = "description", length = 255)
    private String description;

    /**
     * Scope: null = global, set = branch-specific role.
     * Enables multi-branch: a user can be MANAGER at Branch A, STAFF at Branch B.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
