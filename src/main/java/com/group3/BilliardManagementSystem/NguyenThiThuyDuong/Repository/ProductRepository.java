package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository;

import com.group3.BilliardManagementSystem.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
}