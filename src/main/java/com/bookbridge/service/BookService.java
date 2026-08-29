package com.bookbridge.service;

import com.bookbridge.dto.BookRequest;
import com.bookbridge.entity.Book;
import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository repo;
    private final UserRepository userRepo;
    private final ActivityService activityService;

    public BookService(BookRepository repo, UserRepository userRepo, ActivityService activityService) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.activityService = activityService;
    }

    public List<Book> list(String category, String subcategory, String area, Integer limit) {
        List<Book> books = category != null && !category.isBlank()
            ? repo.findByCategory(category)
            : repo.findAll();
        if (subcategory != null && !subcategory.isBlank()) {
            books = books.stream().filter(b -> subcategory.equals(b.getSubcategory())).toList();
        }
        if (area != null && !area.isBlank()) {
            books = books.stream().filter(b -> {
                User owner = userRepo.findById(b.getOwnerId()).orElse(null);
                return owner != null && area.equalsIgnoreCase(owner.getArea());
            }).toList();
        }
        books = new java.util.ArrayList<>(books);
        books.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        if (limit != null && limit > 0 && books.size() > limit) books = books.subList(0, limit);
        return books;
    }

    public Book getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ApiException("Book not found."));
    }

    public List<Book> search(String q) {
        if (q == null || q.isBlank()) return List.of();
        return repo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q)
                .stream().limit(6).toList();
    }

    public List<Book> myBooks(Long ownerId) { return repo.findByOwnerId(ownerId); }

    public Book create(BookRequest req, Long ownerId) {
        User owner = userRepo.findById(ownerId).orElseThrow(() -> new ApiException("Owner not found."));
        Book b = new Book();
        b.setTitle(req.title());
        b.setAuthor(req.author());
        b.setCategory(req.category());
        b.setSubcategory(req.subcategory());
        b.setCondition(req.condition());
        b.setDesc(req.desc());
        b.setImageUrl(req.imageUrl());
        b.setListingType(req.listingType() == null || req.listingType().isBlank() ? "Lend / Borrow" : req.listingType());
        b.setPrice("Sell".equals(b.getListingType()) && req.price() != null && !req.price().isBlank()
            ? Double.valueOf(req.price()) : null);
        b.setOwnerId(ownerId);
        b.setOwner(owner.getName());
        Book saved = repo.save(b);
        activityService.log("book_listed", owner.getName() + " listed \"" + saved.getTitle() + "\" (" + saved.getListingType() + ")");
        return saved;
    }

    public Book update(Long id, BookRequest req, Long ownerId) {
        Book b = getById(id);
        if (!b.getOwnerId().equals(ownerId)) throw new ApiException("You can only edit your own listings.");
        if (req.title() != null) b.setTitle(req.title());
        if (req.author() != null) b.setAuthor(req.author());
        if (req.category() != null) b.setCategory(req.category());
        if (req.subcategory() != null) b.setSubcategory(req.subcategory());
        if (req.condition() != null) b.setCondition(req.condition());
        if (req.desc() != null) b.setDesc(req.desc());
        if (req.imageUrl() != null) b.setImageUrl(req.imageUrl());
        return repo.save(b);
    }

    /** Owner delete, unless the caller is an admin (admin can delete any listing). */
    public void delete(Long id, Long callerId, boolean isAdmin) {
        Book b = getById(id);
        if (!isAdmin && !b.getOwnerId().equals(callerId)) {
            throw new ApiException("You can only delete your own listings.");
        }
        repo.deleteById(id);
        if (isAdmin) activityService.log("book_removed", "Admin removed listing \"" + b.getTitle() + "\"");
    }

    public void adminDelete(Long id) {
        Book b = repo.findById(id).orElse(null);
        repo.deleteById(id);
        activityService.log("book_removed", "Admin removed listing \"" + (b != null ? b.getTitle() : "#" + id) + "\"");
    }
}
