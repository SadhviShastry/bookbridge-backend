package com.bookbridge.controller;

import com.bookbridge.entity.EventItem;
import com.bookbridge.service.EventService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService) { this.eventService = eventService; }

    @GetMapping
    public List<EventItem> list() { return eventService.list(); }
}
