package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.system;

import com.group3.BilliardManagementSystem.ThaiDuyVu.DTO.system.BranchResponse;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.system.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchRepository branchRepository;

    @GetMapping
    public List<BranchResponse> getAll() {

        return branchRepository.findAll()
                .stream()
                .map(branch -> BranchResponse.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .build())
                .toList();
    }
}