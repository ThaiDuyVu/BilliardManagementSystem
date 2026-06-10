package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository;

import com.group3.BilliardManagementSystem.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findBySessionId(Long sessionId);
}