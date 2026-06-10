package com.group3.BilliardManagementSystem.MinhChau.service;

import com.group3.BilliardManagementSystem.Entity.BilliardTable;
import com.group3.BilliardManagementSystem.MinhChau.Realtime.TableRealtimeService;
import com.group3.BilliardManagementSystem.MinhChau.repository.BilliardTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.group3.BilliardManagementSystem.Entity.Branch;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Service
@RequiredArgsConstructor
public class BilliardTableService {

    private final BilliardTableRepository repository;
    private final TableRealtimeService realtimeService;
    private final BranchRepository branchRepository;
    public List<BilliardTable> getAllTables() {
        return repository.findAll();
    }

    public BilliardTable getTableById(Long tableId) {
        return repository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));
    }

    public BilliardTable createTable(BilliardTable table) {
        if (table.getTableType() == null) {
            table.setTableType(BilliardTable.TableType.POOL);
        }

        if (table.getStatus() == null) {
            table.setStatus(BilliardTable.TableStatus.AVAILABLE);
        }

        Branch branch = branchRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));

        table.setBranch(branch);

        LocalDateTime now = LocalDateTime.now();
        table.setCreatedAt(now);
        table.setUpdatedAt(now);
        table.setRatePerMinute(
                table.getRatePerMinute().divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
        );
        BilliardTable savedTable = repository.save(table);
        realtimeService.sendTableUpdate(savedTable);

        return savedTable;
    }

    public BilliardTable updateTable(Long tableId, BilliardTable body) {
        BilliardTable table = getTableById(tableId);

        table.setTableCode(body.getTableCode());
        table.setName(body.getName());
        table.setTableType(body.getTableType());
        table.setRatePerMinute(
                body.getRatePerMinute().divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
        );
        BilliardTable savedTable = repository.save(table);
        realtimeService.sendTableUpdate(savedTable);

        return savedTable;
    }

    public void deleteTable(Long tableId) {
        BilliardTable table = getTableById(tableId);

        if (table.getStatus() == BilliardTable.TableStatus.OCCUPIED ||
                table.getStatus() == BilliardTable.TableStatus.PAUSED) {
            throw new RuntimeException("Không thể xóa bàn đang có phiên chơi");
        }

        repository.delete(table);

        realtimeService.sendTableUpdate(table);
    }
}