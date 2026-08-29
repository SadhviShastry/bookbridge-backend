package com.bookbridge.controller;

import com.bookbridge.dto.ReviewRequest;
import com.bookbridge.entity.Review;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.ReviewService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @GetMapping
    public List<Review> list(@PathVariable Long bookId) { return reviewService.forBook(bookId); }

    @PostMapping
    public Review create(@PathVariable Long bookId, @RequestBody ReviewRequest req) {
        return reviewService.create(bookId, CurrentUser.id(), req.rating(), req.text());
    }
}
