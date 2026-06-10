package com.group3.BilliardManagementSystem.MinhChau.view;

import com.group3.BilliardManagementSystem.MinhChau.service.BilliardTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BilliardViewController {

    private final BilliardTableService tableService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/tables/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("tables", tableService.getAllTables());
        return "tables/dashboard";
    }
}