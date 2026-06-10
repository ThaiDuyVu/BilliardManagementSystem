package com.group3.BilliardManagementSystem.NguyenThiThuyDuong.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/session")
public class POSPageController {

    @GetMapping("/{sessionId}/pos")
    public String openPOSPage(@PathVariable Long sessionId,
                              Model model) {

        model.addAttribute("sessionId", sessionId);

        return "pos-session";
    }
}