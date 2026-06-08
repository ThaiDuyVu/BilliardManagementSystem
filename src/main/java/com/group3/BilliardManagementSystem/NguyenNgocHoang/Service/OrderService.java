package com.group3.BilliardManagementSystem.NguyenNgocHoang.Service;
import com.group3.BilliardManagementSystem.Entity.Order; // Đảm bảo đường dẫn đúng tới file Order.java
import com.group3.BilliardManagementSystem.NguyenNgocHoang.Event.OrderConfirmedEvent; // Đảm bảo đường dẫn đúng tới OrderConfirmedEvent
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Đây là phương thức thực sự sẽ chạy khi đơn hàng được duyệt
    public void confirmOrder(Order order) {
        // 1. Logic lưu đơn hàng vào Database của bạn (ví dụ: orderRepository.save(order))

        // 2. PHÁT SỰ KIỆN để trừ kho
        eventPublisher.publishEvent(new OrderConfirmedEvent(order));

        System.out.println("Đã xác nhận đơn hàng và phát sự kiện trừ kho: " + order.getId());
    }
}