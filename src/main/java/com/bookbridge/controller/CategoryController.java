package com.bookbridge.controller;

import com.bookbridge.dto.CategoryRequest;
import com.bookbridge.entity.Category;
import com.bookbridge.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @GetMapping
    public List<Category> list() { return categoryService.list(); }

    @PostMapping
    public Category create(@RequestBody CategoryRequest req) { return categoryService.create(req); }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) { categoryService.delete(id); }
}
