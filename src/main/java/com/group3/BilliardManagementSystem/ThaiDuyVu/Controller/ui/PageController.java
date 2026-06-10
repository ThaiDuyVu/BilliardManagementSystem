package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // DASHBOARD ROOT
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // EMPLOYEE MODULE
    @GetMapping("/employee-list")
    public String employeeList() {
        return "employee-list";
    }

    @GetMapping("/employee-create")
    public String employeeCreate() {
        return "employee-create";
    }

    // SHIFT MODULE
    @GetMapping("/shift-management")
    public String shiftManagement() {
        return "shift-management";
    }

    @GetMapping("/shift-schedule")
    public String shiftSchedule() {
        return "shift-schedule";
    }
    @GetMapping("/assign-shift")
    public String assignShift() {
        return "assign-shift";
    }
    // AUTH PAGES
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // USERS
    @GetMapping("/users-ui")
    public String users() {
        return "users-ui";
    }

    // TABLES
    @GetMapping("/tables")
    public String tables() {
        return "tables";
    }
}