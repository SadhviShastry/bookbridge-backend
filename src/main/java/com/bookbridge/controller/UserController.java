package com.bookbridge.controller;

import com.bookbridge.entity.User;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public User me() { return userService.sanitize(userService.getById(CurrentUser.id())); }

    @PutMapping("/me")
    public User update(@RequestBody User patch) { return userService.sanitize(userService.update(CurrentUser.id(), patch)); }
}
