package com.bookbridge.service;

import com.bookbridge.entity.Notification;
import com.bookbridge.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) { this.repo = repo; }

    public void push(Long userId, String type, String text, String link) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setText(text);
        n.setLink(link);
        repo.save(n);
    }

    public List<Notification> list(Long userId) { return repo.findTop20ByUserIdOrderByCreatedAtDesc(userId); }
    public long unreadCount(Long userId) { return repo.countByUserIdAndReadFalse(userId); }
    public void markAllRead(Long userId) {
        List<Notification> unread = repo.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        repo.saveAll(unread);
    }
}
