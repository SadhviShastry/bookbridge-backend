package com.bookbridge.service;

import com.bookbridge.dto.ThreadResponse;
import com.bookbridge.entity.Book;
import com.bookbridge.entity.Message;
import com.bookbridge.entity.MessageThread;
import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.MessageRepository;
import com.bookbridge.repository.MessageThreadRepository;
import com.bookbridge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageThreadRepository threadRepo;
    private final MessageRepository messageRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final UserService userService;
    private final NotificationService notificationService;

    public MessageService(MessageThreadRepository threadRepo, MessageRepository messageRepo,
                           BookRepository bookRepo, UserRepository userRepo, UserService userService,
                           NotificationService notificationService) {
        this.threadRepo = threadRepo;
        this.messageRepo = messageRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /** Turns a raw MessageThread into the shape the frontend expects — with the other user and book filled in. */
    private ThreadResponse enrich(MessageThread t, Long viewerId) {
        ThreadResponse dto = new ThreadResponse();
        dto.id = t.getId();
        dto.bookId = t.getBookId();
        dto.book = t.getBookId() != null ? bookRepo.findById(t.getBookId()).orElse(null) : null;

        Long otherId = t.getUserAId().equals(viewerId) ? t.getUserBId() : t.getUserAId();
        User other = userRepo.findById(otherId).orElse(null);
        dto.otherUser = other != null ? userService.sanitize(other) : null;

        dto.lastMessage = t.getLastMessage();
        dto.updatedAt = t.getUpdatedAt();
        return dto;
    }

    public List<ThreadResponse> threadsFor(Long userId) {
        return threadRepo.findByUserAIdOrUserBId(userId, userId).stream()
            .map(t -> enrich(t, userId)).toList();
    }

    public ThreadResponse getOrCreateThread(Long userId, Long ownerId, Long bookId) {
        MessageThread t = threadRepo.findByBookIdAndUserAIdAndUserBId(bookId, userId, ownerId)
            .or(() -> threadRepo.findByBookIdAndUserAIdAndUserBId(bookId, ownerId, userId))
            .orElseGet(() -> {
                MessageThread nt = new MessageThread();
                nt.setUserAId(userId);
                nt.setUserBId(ownerId);
                nt.setBookId(bookId);
                return threadRepo.save(nt);
            });
        return enrich(t, userId);
    }

    public List<Message> messages(Long threadId) { return messageRepo.findByThreadIdOrderBySentAtAsc(threadId); }

    public Message send(Long threadId, Long senderId, String text) {
        MessageThread thread = threadRepo.findById(threadId).orElseThrow(() -> new ApiException("Conversation not found.", HttpStatus.NOT_FOUND));
        Message m = new Message();
        m.setThreadId(threadId);
        m.setSenderId(senderId);
        m.setText(text);
        messageRepo.save(m);

        thread.setLastMessage(text);
        thread.setUpdatedAt(LocalDateTime.now());
        threadRepo.save(thread);

        Long recipientId = thread.getUserAId().equals(senderId) ? thread.getUserBId() : thread.getUserAId();
        notificationService.push(recipientId, "message", "You have a new message", "messages.html?thread=" + threadId);
        return m;
    }
}