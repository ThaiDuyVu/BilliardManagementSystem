package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Controller;

import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.DTO.ProductResponse;
import com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getImageUrl(),
                        p.getSellingPrice()
                ))
                .toList();
    }
}