package com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository;

import com.group3.BilliardManagementSystem.Entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    // THÊM DÒNG NÀY VÀO
    List<InventoryItem> findByNameContainingIgnoreCase(String name);
    boolean existsBySku(String sku);
    boolean existsByNameIgnoreCase(String name);
}