package com.bookbridge.controller;

import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.BadgeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {
    private final BadgeService badgeService;
    public BadgeController(BadgeService badgeService) { this.badgeService = badgeService; }

    @GetMapping("/mine")
    public List<Map<String, String>> mine() { return badgeService.badgesFor(CurrentUser.id()); }
}
