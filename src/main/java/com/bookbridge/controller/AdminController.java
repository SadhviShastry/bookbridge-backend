package com.bookbridge.controller;

import com.bookbridge.dto.RequestResponse;
import com.bookbridge.entity.*;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.UserRepository;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final UserRepository userRepo;
    private final UserService userService;
    private final BookRepository bookRepo;
    private final BookService bookService;
    private final RequestService requestService;
    private final ActivityService activityService;
    private final DonationService donationService;

    public AdminController(AdminService adminService, UserRepository userRepo, UserService userService,
                            BookRepository bookRepo, BookService bookService, RequestService requestService,
                            ActivityService activityService, DonationService donationService) {
        this.adminService = adminService;
        this.userRepo = userRepo;
        this.userService = userService;
        this.bookRepo = bookRepo;
        this.bookService = bookService;
        this.requestService = requestService;
        this.activityService = activityService;
        this.donationService = donationService;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() { return adminService.stats(); }

    @GetMapping("/users")
    public List<User> users() { return userRepo.findAll().stream().map(userService::sanitize).toList(); }

    @DeleteMapping("/users/{id}")
    public void removeUser(@PathVariable Long id) { userService.delete(id); }

    @GetMapping("/books")
    public List<Book> books() { return bookRepo.findAll(); }

    @DeleteMapping("/books/{id}")
    public void removeBook(@PathVariable Long id) { bookService.delete(id, CurrentUser.id(), true); }

    @GetMapping("/exchanges")
    public List<RequestResponse> exchanges() { return requestService.acceptedAndCompleted(); }

    @GetMapping("/activity")
    public List<ActivityLog> activity() { return activityService.recent(); }

    @GetMapping("/donations")
    public List<Donation> donations() { return donationService.list(); }
}