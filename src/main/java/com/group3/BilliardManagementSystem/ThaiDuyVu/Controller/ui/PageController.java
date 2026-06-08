package com.group3.BilliardManagementSystem.ThaiDuyVu.Controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/shift-management")
    public String shiftManagement() {
        return "shift-management";
    }
    @GetMapping("/users-ui")
    public String users() {
        return "users-ui";
    }
    @GetMapping("/employee-create")
    public String create() {
        return "employee-create";
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @GetMapping("/employee-list")
    public String list() {
        return "employee-list";
    }
    @GetMapping("/assign-shift")
    public String assignShift() {
        return "assign-shift";
    }
    @GetMapping("/shift-schedule")
    public String view() {
        return "shift-schedule";
    }
}