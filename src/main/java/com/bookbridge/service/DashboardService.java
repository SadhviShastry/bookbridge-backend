package com.bookbridge.service;

import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.BorrowRequestRepository;
import com.bookbridge.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final BookRepository bookRepo;
    private final BorrowRequestRepository requestRepo;
    private final ReviewRepository reviewRepo;

    public DashboardService(BookRepository bookRepo, BorrowRequestRepository requestRepo, ReviewRepository reviewRepo) {
        this.bookRepo = bookRepo;
        this.requestRepo = requestRepo;
        this.reviewRepo = reviewRepo;
    }

    public Map<String, Object> summary(Long userId) {
        long booksShared = bookRepo.findByOwnerId(userId).size();
        long booksBorrowed = requestRepo.findByRequesterId(userId).stream()
            .filter(r -> r.getStatus().equals("Accepted") || r.getStatus().equals("Completed")).count();
        long pendingRequests = requestRepo.findByRequesterId(userId).stream().filter(r -> r.getStatus().equals("Pending")).count()
            + requestRepo.findByOwnerId(userId).stream().filter(r -> r.getStatus().equals("Pending")).count();
        long reviewsGiven = reviewRepo.findByUserId(userId).size();

        Map<String, Object> m = new HashMap<>();
        m.put("booksShared", (int) booksShared);
        m.put("booksBorrowed", (int) booksBorrowed);
        m.put("pendingRequests", (int) pendingRequests);
        m.put("reviewsGiven", (int) reviewsGiven);
        return m;
    }
}
