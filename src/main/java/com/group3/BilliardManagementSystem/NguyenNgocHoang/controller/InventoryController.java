package com.group3.BilliardManagementSystem.NguyenNgocHoang.controller;

import com.group3.BilliardManagementSystem.Entity.InventoryItem;
import com.group3.BilliardManagementSystem.Entity.RevenueReportLog;
import com.group3.BilliardManagementSystem.Entity.StockMovement;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Service.InventoryService;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository.InventoryItemRepository;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository.RevenueReportLogRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired private InventoryService inventoryService;
    @Autowired private BranchRepository branchRepository;
    @Autowired private InventoryItemRepository inventoryItemRepository;
    @Autowired private RevenueReportLogRepository revenueRepo;

    // --- DASHBOARD (NƠI ĐỂ TEST) ---
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(value = "keyword", required = false) String keyword,
                                HttpSession session, Model model) {

     //test phân quyền nha VŨ
        session.setAttribute("userRole", "ROLE_ADMIN");

        // Truyền quyền xuống giao diện
        model.addAttribute("userRole", session.getAttribute("userRole"));

        List<InventoryItem> items = (keyword != null && !keyword.isEmpty())
                ? inventoryService.searchItems(keyword)
                : inventoryService.getAllInventoryItems();

        long lowStockCount = items.stream()
                .filter(i -> i.getCurrentStock() != null && i.getCurrentStock().compareTo(BigDecimal.valueOf(100)) < 0)
                .count();

        model.addAttribute("items", items);
        model.addAttribute("newItem", new InventoryItem());
        model.addAttribute("keyword", keyword);
        model.addAttribute("lowStockCount", lowStockCount);
        return "inventory-dashboard";
    }

    // --- THÊM SẢN PHẨM ---
    @PostMapping("/add")
    public String addInventoryItem(@ModelAttribute("newItem") InventoryItem newItem) {
        if (newItem.getBranch() == null) {
            branchRepository.findAll().stream().findFirst().ifPresent(newItem::setBranch);
        }
        if (newItem.getSku() == null || newItem.getSku().isEmpty()) {
            newItem.setSku("SKU-" + System.currentTimeMillis());
        }
        if (newItem.getStatus() == null) {
            newItem.setStatus(InventoryItem.ItemStatus.ACTIVE);
        }

        inventoryService.save(newItem);
        inventoryService.logMovement(newItem, StockMovement.MovementType.INBOUND, newItem.getCurrentStock(), "Nhập kho mới");
        return "redirect:/inventory/dashboard";
    }

    // --- XÓA SẢN PHẨM (ĐÃ CHẶN QUYỀN) ---
    @GetMapping("/delete/{id}")
    public String deleteInventoryItem(@PathVariable("id") Long id, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        // Kiểm tra chặn truy cập URL trái phép
        if (!"ROLE_ADMIN".equals(role)) {
            return "redirect:/inventory/dashboard?error=unauthorized";
        }

        inventoryItemRepository.findById(id).ifPresent(item -> {
            inventoryService.logMovement(item, StockMovement.MovementType.WASTE, item.getCurrentStock(), "Xóa sản phẩm khỏi hệ thống");
            inventoryService.deleteById(id);
        });
        return "redirect:/inventory/dashboard";
    }

    // --- BÁO CÁO DOANH THU (ĐÃ CHẶN QUYỀN) ---
    @GetMapping("/report")
    public String showReport(HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        // Chỉ Admin và Manager mới được xem
        if (!"ROLE_ADMIN".equals(role) && !"ROLE_MANAGER".equals(role)) {
            return "redirect:/inventory/dashboard?error=access-denied";
        }

        model.addAttribute("reports", revenueRepo.findAll());
        return "inventory-report";
    }

    @GetMapping("/api/revenue-stats")
    @ResponseBody
    public List<RevenueReportLog> getRevenueStats() {
        return revenueRepo.findTop7ByOrderByReportDateDesc();
    }

    @GetMapping("/report/test-data")
    @ResponseBody
    public String createTestData() {
        for (int i = 0; i < 3; i++) {
            RevenueReportLog log = new RevenueReportLog();
            log.setReportDate(LocalDate.now().minusDays(i));
            log.setGrossRevenue(BigDecimal.valueOf(1000000 + (i * 500000)));
            log.setStatus(RevenueReportLog.ReportStatus.GENERATED);
            revenueRepo.save(log);
        }
        return "Đã thêm dữ liệu mẫu! Quay lại <a href='/inventory/report'>Báo cáo</a>";
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=danh_sach_ton_kho.xlsx");
        List<InventoryItem> items = inventoryService.getAllInventoryItems();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tồn kho");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tên sản phẩm");
            header.createCell(1).setCellValue("Tồn kho");
            header.createCell(2).setCellValue("Đơn vị");
            for (int i = 0; i < items.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(items.get(i).getName());
                row.createCell(1).setCellValue(items.get(i).getCurrentStock() != null ? items.get(i).getCurrentStock().doubleValue() : 0);
                row.createCell(2).setCellValue(items.get(i).getUnit());
            }
            workbook.write(response.getOutputStream());
        }
    }
}