package com.bookbridge.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BadgeService {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final DashboardService dashboardService;

    public BadgeService(BookService bookService, ReviewService reviewService, DashboardService dashboardService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.dashboardService = dashboardService;
    }

    public List<Map<String, String>> badgesFor(Long userId) {
        var stats = dashboardService.summary(userId);
        List<Map<String, String>> badges = new ArrayList<>();
        int booksShared = (int) stats.get("booksShared");
        int booksBorrowed = (int) stats.get("booksBorrowed");
        long reviewsGiven = reviewService.countByUser(userId);

        if (booksShared >= 5) badges.add(Map.of("id", "sharer_5", "label", "Top Sharer", "icon", "fa-book"));
        else if (booksShared >= 1) badges.add(Map.of("id", "sharer_1", "label", "First Share", "icon", "fa-seedling"));

        if (reviewsGiven >= 5) badges.add(Map.of("id", "reviewer_5", "label", "5-Star Reviewer", "icon", "fa-star"));
        else if (reviewsGiven >= 1) badges.add(Map.of("id", "reviewer_1", "label", "First Review", "icon", "fa-comment"));

        if (booksBorrowed >= 3) badges.add(Map.of("id", "reader_3", "label", "Avid Reader", "icon", "fa-glasses"));
        else if (booksBorrowed >= 1) badges.add(Map.of("id", "reader_1", "label", "First Borrow", "icon", "fa-hand-holding-heart"));

        return badges;
    }
}
