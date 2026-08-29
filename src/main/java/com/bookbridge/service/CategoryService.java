package com.bookbridge.service;

import com.bookbridge.dto.CategoryRequest;
import com.bookbridge.entity.Category;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;
    private final BookRepository bookRepo;
    private final ActivityService activityService;

    public CategoryService(CategoryRepository repo, BookRepository bookRepo, ActivityService activityService) {
        this.repo = repo;
        this.bookRepo = bookRepo;
        this.activityService = activityService;
    }

    public List<Category> list() { return repo.findAll(); }

    public Category create(CategoryRequest req) {
        repo.findByNameIgnoreCase(req.name()).ifPresent(c -> {
            throw new ApiException("This category already exists.");
        });
        Category c = new Category();
        c.setName(req.name());
        c.setIcon(req.icon() == null || req.icon().isBlank() ? "fa-book" : req.icon());
        Category saved = repo.save(c);
        activityService.log("category_added", "Admin added category \"" + saved.getName() + "\"");
        return saved;
    }

    public void delete(Long id) {
        Category c = repo.findById(id).orElseThrow(() -> new ApiException("Category not found."));
        long inUse = bookRepo.countByCategory(c.getName());
        if (inUse > 0) {
            throw new ApiException("Can't delete \"" + c.getName() + "\" — books are still listed under it.");
        }
        repo.deleteById(id);
        activityService.log("category_removed", "Admin removed category \"" + c.getName() + "\"");
    }
}
