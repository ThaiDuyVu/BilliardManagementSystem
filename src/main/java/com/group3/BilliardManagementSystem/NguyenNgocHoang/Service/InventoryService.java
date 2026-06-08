package com.group3.BilliardManagementSystem.NguyenNgocHoang.Service;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository.InventoryItemRepository;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository.RevenueReportLogRepository;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryRepo;
    private final StockMovementRepository movementRepo;
    private final RevenueReportLogRepository revenueRepo;

    /**
     * Hàm ghi nhật ký biến động kho dùng chung (Audit Log)
     * Tự động tính toán số dư sau giao dịch (after)
     */
    @Transactional
    public void logMovement(InventoryItem item, StockMovement.MovementType type, BigDecimal qty, String note) {
        BigDecimal before = item.getCurrentStock();
        BigDecimal after = (type == StockMovement.MovementType.INBOUND || type == StockMovement.MovementType.TRANSFER_IN)
                ? before.add(qty) : before.subtract(qty);

        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại

        StockMovement log = StockMovement.builder()
                .inventoryItem(item)
                .movementType(type)
                .quantity(qty)
                .stockBefore(before)
                .stockAfter(after)
                .movementTime(now)
                .note(note)
                .build();

        movementRepo.save(log);
    }

    /**
     * Xử lý trừ kho và ghi lịch sử khi đơn hàng được xác nhận
     */
    @Transactional
    public void processOrderOutbound(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) return;

        for (OrderItem item : order.getItems()) {
            InventoryItem invItem = item.getProduct().getInventoryItem();
            if (invItem == null) continue;

            BigDecimal qtyChange = BigDecimal.valueOf(item.getQuantity());

            // 1. Ghi nhật ký (Sử dụng hàm logMovement để tái sử dụng logic)
            logMovement(invItem, StockMovement.MovementType.OUTBOUND, qtyChange, "Order #" + order.getId());

            // 2. Cập nhật tồn kho thực tế
            invItem.setCurrentStock(invItem.getCurrentStock().subtract(qtyChange));
            inventoryRepo.save(invItem);
        }

        // 3. Ghi báo cáo doanh thu
        generateRevenueReport(order);
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     */
    public List<InventoryItem> searchItems(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return inventoryRepo.findByNameContainingIgnoreCase(keyword);
        }
        return inventoryRepo.findAll();
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm
     */
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepo.findAll();
    }

    /**
     * Lưu mới hoặc cập nhật sản phẩm
     */
    public void save(InventoryItem item) {
        inventoryRepo.save(item);
    }

    /**
     * Xóa sản phẩm
     */
    @Transactional
    public void deleteById(Long id) {
        inventoryRepo.deleteById(id);
    }

    /**
     * Lưu log doanh thu khi đơn hàng thành công
     */
    public void generateRevenueReport(Order order) {
        RevenueReportLog report = RevenueReportLog.builder()
                .reportDate(LocalDate.now())
                .grossRevenue(order.getTotalAmount())
                .status(RevenueReportLog.ReportStatus.GENERATED)
                .build();
        revenueRepo.save(report);
    }
}