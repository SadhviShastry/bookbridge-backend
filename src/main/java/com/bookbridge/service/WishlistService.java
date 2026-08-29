package com.bookbridge.service;

import com.bookbridge.entity.Book;
import com.bookbridge.entity.WishlistItem;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {
    private final WishlistItemRepository repo;
    private final BookRepository bookRepo;

    public WishlistService(WishlistItemRepository repo, BookRepository bookRepo) {
        this.repo = repo;
        this.bookRepo = bookRepo;
    }

    public List<Book> list(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(w -> bookRepo.findById(w.getBookId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public boolean isWishlisted(Long userId, Long bookId) {
        return repo.findByUserIdAndBookId(userId, bookId).isPresent();
    }

    public boolean toggle(Long userId, Long bookId) {
        var existing = repo.findByUserIdAndBookId(userId, bookId);
        if (existing.isPresent()) {
            repo.delete(existing.get());
            return false;
        }
        WishlistItem w = new WishlistItem();
        w.setUserId(userId);
        w.setBookId(bookId);
        repo.save(w);
        return true;
    }
}
