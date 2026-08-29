package com.bookbridge.controller;

import com.bookbridge.dto.DonationRequest;
import com.bookbridge.entity.Donation;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.DonationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;
    public DonationController(DonationService donationService) { this.donationService = donationService; }

    @PostMapping
    public Donation create(@RequestBody DonationRequest req) {
        return donationService.create(CurrentUser.id(), req.amount(), req.message());
    }

    @GetMapping
    public List<Donation> list() { return donationService.list(); }
}
