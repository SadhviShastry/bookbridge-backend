package com.bookbridge.service;

import com.bookbridge.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {
    private final UserRepository userRepo;
    private final BookRepository bookRepo;
    private final BorrowRequestRepository requestRepo;
    private final DonationRepository donationRepo;

    public AdminService(UserRepository userRepo, BookRepository bookRepo,
                         BorrowRequestRepository requestRepo, DonationRepository donationRepo) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.requestRepo = requestRepo;
        this.donationRepo = donationRepo;
    }

    public Map<String, Object> stats() {
        Map<String, Object> m = new HashMap<>();
        m.put("totalUsers", userRepo.findByRole("user").size());
        m.put("totalBooks", bookRepo.count());
        m.put("pendingRequests", requestRepo.countByStatus("Pending"));
        m.put("totalExchanges", requestRepo.countByStatus("Completed"));
        double totalDonations = donationRepo.findAll().stream().mapToDouble(d -> d.getAmount()).sum();
        m.put("totalDonations", totalDonations);
        m.put("donationCount", donationRepo.count());
        return m;
    }
}
