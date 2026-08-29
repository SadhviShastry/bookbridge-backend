package com.bookbridge.controller;

import com.bookbridge.entity.Notification;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) { this.notificationService = notificationService; }

    @GetMapping
    public List<Notification> list() { return notificationService.list(CurrentUser.id()); }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount() { return Map.of("count", notificationService.unreadCount(CurrentUser.id())); }

    @PutMapping("/mark-read")
    public void markAllRead() { notificationService.markAllRead(CurrentUser.id()); }
}
