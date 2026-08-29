package com.bookbridge.service;

import com.bookbridge.entity.Book;
import com.bookbridge.entity.Review;
import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.ReviewRepository;
import com.bookbridge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository repo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public ReviewService(ReviewRepository repo, BookRepository bookRepo, UserRepository userRepo,
                          NotificationService notificationService, ActivityService activityService) {
        this.repo = repo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    public List<Review> forBook(Long bookId) { return repo.findByBookIdOrderByCreatedAtDesc(bookId); }

    public Review create(Long bookId, Long userId, Integer rating, String text) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new ApiException("Book not found.", HttpStatus.NOT_FOUND));
        User user = userRepo.findById(userId).orElseThrow(() -> new ApiException("User not found.", HttpStatus.NOT_FOUND));
        if (rating == null || rating < 1 || rating > 5) throw new ApiException("Please select a star rating.");
        if (text == null || text.trim().length() < 5) throw new ApiException("Please write a short review (at least 5 characters).");

        Review review = new Review();
        review.setBookId(bookId);
        review.setUserId(userId);
        review.setUserName(user.getName());
        review.setRating(rating);
        review.setText(text);
        repo.save(review);

        List<Review> all = repo.findByBookIdOrderByCreatedAtDesc(bookId);
        double avg = all.stream().mapToInt(Review::getRating).average().orElse(0);
        book.setReviews(all.size());
        book.setRating(Math.round(avg * 10.0) / 10.0);
        bookRepo.save(book);

        if (!book.getOwnerId().equals(userId)) {
            notificationService.push(book.getOwnerId(), "review",
                user.getName() + " left a " + rating + "\u2605 review on \"" + book.getTitle() + "\"",
                "book-details.html?id=" + book.getId());
        }
        activityService.log("review_added", user.getName() + " left a " + rating + "\u2605 review on \"" + book.getTitle() + "\"");
        return review;
    }

    public long countByUser(Long userId) { return repo.findByUserId(userId).size(); }
}
