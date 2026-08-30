package com.bookbridge.controller;

import com.bookbridge.dto.MessageSendRequest;
import com.bookbridge.dto.MessageStartRequest;
import com.bookbridge.dto.ThreadResponse;
import com.bookbridge.entity.Message;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.MessageService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    public MessageController(MessageService messageService) { this.messageService = messageService; }

    @GetMapping("/threads")
    public List<ThreadResponse> threads() { return messageService.threadsFor(CurrentUser.id()); }

    @PostMapping("/threads")
    public ThreadResponse startThread(@RequestBody MessageStartRequest req) {
        return messageService.getOrCreateThread(CurrentUser.id(), req.ownerId(), req.bookId());
    }

    @GetMapping("/threads/{threadId}")
    public List<Message> messages(@PathVariable Long threadId) { return messageService.messages(threadId); }

    @PostMapping("/threads/{threadId}")
    public Message send(@PathVariable Long threadId, @RequestBody MessageSendRequest req) {
        return messageService.send(threadId, CurrentUser.id(), req.text());
    }
}