package com.group3.BilliardManagementSystem.MinhChau.config;

import com.group3.BilliardManagementSystem.Entity.BilliardTable;
import com.group3.BilliardManagementSystem.Entity.Branch;
import com.group3.BilliardManagementSystem.MinhChau.repository.BilliardTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TableSeeder implements CommandLineRunner {

    private final BilliardTableRepository billiardTableRepository;
    private final BranchRepository branchRepository;

    @Override
    public void run(String... args) {

        if (billiardTableRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        Branch branch = branchRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Branch b = new Branch();
                    b.setName("Chi nhánh chính");
                    b.setAddress("Core Billiard");
                    b.setPhone("0900000000");
                    b.setStatus(Branch.BranchStatus.ACTIVE);
                    b.setCreatedAt(now);
                    b.setUpdatedAt(now);
                    return branchRepository.save(b);
                });

        billiardTableRepository.saveAll(List.of(
                createTable("B01", "Bàn Pool 1", 2000, BilliardTable.TableType.POOL, branch, now),
                createTable("B02", "Bàn Pool 2", 2000, BilliardTable.TableType.POOL, branch, now),
                createTable("B03", "Bàn Pool 3", 2500, BilliardTable.TableType.POOL, branch, now),
                createTable("B04", "Bàn Pool 4", 2500, BilliardTable.TableType.POOL, branch, now),
                createTable("B05", "Bàn Carom 1", 3000, BilliardTable.TableType.CAROM, branch, now),
                createTable("B06", "Bàn Carom 2", 3000, BilliardTable.TableType.CAROM, branch, now),
                createTable("B07", "Bàn Carom 3", 3000, BilliardTable.TableType.CAROM, branch, now),
                createTable("B08", "Bàn Carom 4", 3000, BilliardTable.TableType.CAROM, branch, now)
        ));
    }

    private BilliardTable createTable(
            String code,
            String name,
            int ratePerMinute,
            BilliardTable.TableType tableType,
            Branch branch,
            LocalDateTime now
    ) {
        BilliardTable table = new BilliardTable();

        table.setTableCode(code);
        table.setName(name);
        table.setRatePerMinute(BigDecimal.valueOf(ratePerMinute));
        table.setTableType(tableType);
        table.setStatus(BilliardTable.TableStatus.AVAILABLE);
        table.setBranch(branch);
        table.setCreatedAt(now);
        table.setUpdatedAt(now);

        return table;
    }
}