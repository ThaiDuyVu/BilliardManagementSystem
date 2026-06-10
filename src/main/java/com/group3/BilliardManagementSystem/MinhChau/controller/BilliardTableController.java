package com.group3.BilliardManagementSystem.MinhChau.controller;

import com.group3.BilliardManagementSystem.Entity.BilliardTable;
import com.group3.BilliardManagementSystem.MinhChau.service.BilliardTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BilliardTableController {

    private final BilliardTableService tableService;

    @GetMapping
    public List<BilliardTableResponse> getAll() {
        return tableService.getAllTables()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{tableId}")
    public BilliardTableResponse getById(@PathVariable Long tableId) {
        return toResponse(tableService.getTableById(tableId));
    }

    @PostMapping
    public BilliardTableResponse create(@RequestBody BilliardTable table) {
        return toResponse(tableService.createTable(table));
    }

    @PutMapping("/{tableId}")
    public BilliardTableResponse update(
            @PathVariable Long tableId,
            @RequestBody BilliardTable body
    ) {
        return toResponse(tableService.updateTable(tableId, body));
    }

    @DeleteMapping("/{tableId}")
    public String delete(@PathVariable Long tableId) {
        tableService.deleteTable(tableId);
        return "Đã xóa bàn";
    }

    private BilliardTableResponse toResponse(BilliardTable table) {
        return new BilliardTableResponse(
                table.getId(),
                table.getTableCode(),
                table.getName(),
                table.getStatus().name(),
                table.getTableType().name(),
                table.getRatePerMinute()
        );
    }

    public record BilliardTableResponse(
            Long id,
            String tableCode,
            String name,
            String status,
            String tableType,
            BigDecimal ratePerMinute
    ) {}
}