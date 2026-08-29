package com.bookbridge.controller;

import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.DashboardService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }

    @GetMapping("/summary")
    public Map<String, Object> summary() { return dashboardService.summary(CurrentUser.id()); }
}
