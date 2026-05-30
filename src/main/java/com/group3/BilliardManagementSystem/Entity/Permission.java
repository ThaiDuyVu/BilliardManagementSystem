package com.group3.BilliardManagementSystem.Entity;

import com.group3.BilliardManagementSystem.Entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Fine-grained permission unit (e.g., TABLE_CREATE, ORDER_APPROVE).
 * Permissions are grouped into Roles using a many-to-many relationship.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique permission code used in Spring Security checks.
     * Format: RESOURCE_ACTION (e.g., TABLE_READ, INVOICE_APPROVE)
     */
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "module", length = 50)
    private String module; // e.g., "BILLIARD", "FNB", "INVENTORY", "HR"

    @ManyToMany(mappedBy = "permissions")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
