package com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository;

import com.group3.BilliardManagementSystem.Entity.RevenueReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // THÊM DÒNG NÀY VÀO

@Repository
public interface RevenueReportLogRepository extends JpaRepository<RevenueReportLog, Long> {
    // Lấy 7 báo cáo mới nhất để vẽ biểu đồ
    List<RevenueReportLog> findTop7ByOrderByReportDateDesc();
}