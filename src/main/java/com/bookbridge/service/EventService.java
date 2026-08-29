package com.bookbridge.service;

import com.bookbridge.entity.EventItem;
import com.bookbridge.repository.EventItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {
    private final EventItemRepository repo;
    public EventService(EventItemRepository repo) { this.repo = repo; }
    public List<EventItem> list() { return repo.findAllByOrderByDateAsc(); }
}
