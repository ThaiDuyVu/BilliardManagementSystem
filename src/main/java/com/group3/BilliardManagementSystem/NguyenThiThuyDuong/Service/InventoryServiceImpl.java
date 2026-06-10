package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Service;

import com.group3.BilliardManagementSystem.Entity.*;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public void deductStock(Order order) {

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            InventoryItem inventory = product.getInventoryItem();

            if (inventory == null) continue;

            BigDecimal deductQty =
                    product.getStockDeductionQty()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

            inventory.setCurrentStock(
                    inventory.getCurrentStock().subtract(deductQty)
            );

            inventoryItemRepository.save(inventory);
        }
    }
}