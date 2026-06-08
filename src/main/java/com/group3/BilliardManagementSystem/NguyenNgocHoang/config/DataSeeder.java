package com.group3.BilliardManagementSystem.NguyenNgocHoang.config;

import com.group3.BilliardManagementSystem.Entity.Branch;
import com.group3.BilliardManagementSystem.Entity.InventoryItem;
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Repository.InventoryItemRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final InventoryItemRepository itemRepo;
    private final BranchRepository branchRepo;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println(">>> [DataSeeder] Đang kiểm tra dữ liệu tồn kho...");

        Branch branch = getExistingBranch();

        createItemIfNotExists(
                "CUE-001",
                "Cơ bida Adam",
                BigDecimal.valueOf(50),
                "Cái",
                branch
        );

        createItemIfNotExists(
                "BALL-001",
                "Bóng bida Aramith",
                BigDecimal.valueOf(200),
                "Bộ",
                branch
        );

        createItemIfNotExists(
                "GLOVE-001",
                "Găng tay bida",
                BigDecimal.valueOf(100),
                "Đôi",
                branch
        );

        System.out.println(">>> [DataSeeder] Hoàn tất khởi tạo dữ liệu.");
    }

    private Branch getExistingBranch() {

        return branchRepo.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy Branch nào trong hệ thống. Hãy seed Branch trước."
                        ));
    }

    private void createItemIfNotExists(
            String sku,
            String itemName,
            BigDecimal quantity,
            String unit,
            Branch branch
    ) {

        boolean exists =
                itemRepo.existsBySku(sku)
                        || itemRepo.existsByNameIgnoreCase(itemName);

        if (exists) {
            System.out.println(">>> Sản phẩm đã tồn tại: " + itemName);
            return;
        }

        InventoryItem item = InventoryItem.builder()
                .sku(sku)
                .name(itemName)
                .description(itemName)
                .unit(unit)
                .currentStock(quantity)
                .reorderLevel(BigDecimal.TEN)
                .costPrice(BigDecimal.ZERO)
                .branch(branch)
                .build();

        itemRepo.save(item);

        System.out.println(">>> Đã tạo sản phẩm: " + itemName);
    }
}