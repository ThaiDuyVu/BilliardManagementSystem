package com.group3.BilliardManagementSystem.NguyenNgocHoang.Service;

import com.group3.BilliardManagementSystem.NguyenNgocHoang.Event.OrderConfirmedEvent; // Import đúng đường dẫn
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InventoryDeductionListener {

    private final InventoryService inventoryService;

    @EventListener

    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        inventoryService.processOrderOutbound(event.getOrder());
    }
}