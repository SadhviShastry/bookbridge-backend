package com.bookbridge.controller;

import com.bookbridge.dto.BookRequest;
import com.bookbridge.entity.Book;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.BookService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) { this.bookService = bookService; }

    @GetMapping
    public List<Book> list(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String subcategory,
                            @RequestParam(required = false) String area,
                            @RequestParam(required = false) Integer limit) {
        return bookService.list(category, subcategory, area, limit);
    }

    @GetMapping("/mine")
    public List<Book> mine() { return bookService.myBooks(CurrentUser.id()); }

    @GetMapping("/search")
    public List<Book> search(@RequestParam String q) { return bookService.search(q); }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) { return bookService.getById(id); }

    @PostMapping
    public Book create(@RequestBody BookRequest req) { return bookService.create(req, CurrentUser.id()); }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @RequestBody BookRequest req) { return bookService.update(id, req, CurrentUser.id()); }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) { bookService.delete(id, CurrentUser.id(), CurrentUser.isAdmin()); }
}
