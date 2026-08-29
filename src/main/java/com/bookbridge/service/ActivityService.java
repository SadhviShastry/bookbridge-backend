package com.bookbridge.service;

import com.bookbridge.entity.ActivityLog;
import com.bookbridge.repository.ActivityLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityLogRepository repo;

    public ActivityService(ActivityLogRepository repo) { this.repo = repo; }

    public void log(String type, String text) {
        ActivityLog a = new ActivityLog();
        a.setType(type);
        a.setText(text);
        repo.save(a);
    }

    public List<ActivityLog> recent() {
        return repo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 200));
    }
}
