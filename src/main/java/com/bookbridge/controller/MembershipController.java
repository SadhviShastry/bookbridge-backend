package com.bookbridge.controller;

import com.bookbridge.dto.MembershipJoinRequest;
import com.bookbridge.entity.User;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.MembershipService;
import com.bookbridge.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {
    private final MembershipService membershipService;
    private final UserService userService;

    public MembershipController(MembershipService membershipService, UserService userService) {
        this.membershipService = membershipService;
        this.userService = userService;
    }

    @GetMapping("/tiers")
    public List<Map<String, Object>> tiers() { return membershipService.tiers(); }

    @PostMapping("/join")
    public User join(@RequestBody MembershipJoinRequest req) {
        return userService.sanitize(membershipService.join(CurrentUser.id(), req.tierId()));
    }
}
