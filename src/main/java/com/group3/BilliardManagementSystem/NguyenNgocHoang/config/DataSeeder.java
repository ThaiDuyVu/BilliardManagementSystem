package com.group3.BilliardManagementSystem.NguyenNgocHoang.config;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.InventoryItemRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.CategoryRepository;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.ProductRepository;
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
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println(">>> [DataSeeder] START SYSTEM SEED...");

        Branch branch = getExistingBranch();

        // =========================
        // INVENTORY (CHECK ONLY SKU)
        // =========================
        createItemIfNotExists("CUE-001", "Cơ bida Adam",
                BigDecimal.valueOf(50), "Cái", branch);

        createItemIfNotExists("BALL-001", "Bóng bida Aramith",
                BigDecimal.valueOf(200), "Bộ", branch);

        createItemIfNotExists("GLOVE-001", "Găng tay bida",
                BigDecimal.valueOf(100), "Đôi", branch);

        // =========================
        // CATEGORY
        // =========================
        Category drinks = saveCategory("Đồ Uống");
        Category foods = saveCategory("Đồ Ăn");
        Category combos = saveCategory("Combo");
        Category billiards = saveCategory("Dịch Vụ Bida");
        Category tools = saveCategory("Phụ Kiện");

        // =========================
        // PRODUCT (CHECK ONLY SKU)
        // =========================

        saveProductIfNotExists("DRINK-001", "Cafe đen", "Tỉnh táo", 25000, drinks);
        saveProductIfNotExists("DRINK-002", "Cafe sữa", "Classic", 30000, drinks);
        saveProductIfNotExists("DRINK-003", "Coca Cola", "Giải khát", 20000, drinks);
        saveProductIfNotExists("DRINK-004", "Redbull", "Tăng lực", 25000, drinks);

        saveProductIfNotExists("FOOD-001", "Khoai tây chiên", "Snack", 39000, foods);
        saveProductIfNotExists("FOOD-002", "Cá viên chiên", "Snack", 35000, foods);

        saveProductIfNotExists("COMBO-001", "Combo Sinh Viên", "2h bida + nước", 149000, combos);
        saveProductIfNotExists("COMBO-002", "Combo Cày Kèo", "3h bida + đồ ăn", 249000, combos);

        saveProductIfNotExists("SERVICE-001", "Bida thường", "79k/giờ", 79000, billiards);
        saveProductIfNotExists("SERVICE-002", "Bida VIP", "119k/giờ", 119000, billiards);

        saveProductIfNotExists("TOOL-001", "Thuê cơ bida", "Dụng cụ", 30000, tools);

        System.out.println(">>> [DataSeeder] DONE SUCCESS");
    }

    // =========================
    // BRANCH
    // =========================
    private Branch getExistingBranch() {
        return branchRepo.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Branch found"));
    }

    // =========================
    // INVENTORY
    // =========================
    private void createItemIfNotExists(
            String sku,
            String itemName,
            BigDecimal quantity,
            String unit,
            Branch branch
    ) {
        if (itemRepo.existsBySku(sku)) {
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
    }

    // =========================
    // CATEGORY
    // =========================
    private Category saveCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    return categoryRepository.save(c);
                });
    }

    // =========================
    // PRODUCT (ONLY SKU CHECK)
    // =========================
    private void saveProductIfNotExists(
            String sku,
            String name,
            String description,
            double price,
            Category category
    ) {
        if (productRepository.existsBySku(sku)) {
            return;
        }

        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setDescription(description);
        product.setSellingPrice(BigDecimal.valueOf(price));
        product.setCategory(category);
        product.setStatus(Product.ProductStatus.AVAILABLE);

        productRepository.save(product);
    }
}