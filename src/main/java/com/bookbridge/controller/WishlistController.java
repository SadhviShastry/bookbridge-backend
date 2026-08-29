package com.bookbridge.controller;

import com.bookbridge.entity.Book;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.WishlistService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    public WishlistController(WishlistService wishlistService) { this.wishlistService = wishlistService; }

    @GetMapping
    public List<Book> list() { return wishlistService.list(CurrentUser.id()); }

    @GetMapping("/{bookId}/status")
    public Map<String, Boolean> status(@PathVariable Long bookId) {
        return Map.of("wishlisted", wishlistService.isWishlisted(CurrentUser.id(), bookId));
    }

    @PostMapping("/{bookId}/toggle")
    public Map<String, Boolean> toggle(@PathVariable Long bookId) {
        return Map.of("added", wishlistService.toggle(CurrentUser.id(), bookId));
    }
}
