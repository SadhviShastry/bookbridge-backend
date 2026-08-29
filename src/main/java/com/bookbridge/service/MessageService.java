package com.bookbridge.service;

import com.bookbridge.entity.Message;
import com.bookbridge.entity.MessageThread;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.MessageRepository;
import com.bookbridge.repository.MessageThreadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageThreadRepository threadRepo;
    private final MessageRepository messageRepo;
    private final NotificationService notificationService;

    public MessageService(MessageThreadRepository threadRepo, MessageRepository messageRepo,
                           NotificationService notificationService) {
        this.threadRepo = threadRepo;
        this.messageRepo = messageRepo;
        this.notificationService = notificationService;
    }

    public List<MessageThread> threadsFor(Long userId) {
        return threadRepo.findByUserAIdOrUserBId(userId, userId);
    }

    public MessageThread getOrCreateThread(Long userId, Long ownerId, Long bookId) {
        return threadRepo.findByBookIdAndUserAIdAndUserBId(bookId, userId, ownerId)
            .or(() -> threadRepo.findByBookIdAndUserAIdAndUserBId(bookId, ownerId, userId))
            .orElseGet(() -> {
                MessageThread t = new MessageThread();
                t.setUserAId(userId);
                t.setUserBId(ownerId);
                t.setBookId(bookId);
                return threadRepo.save(t);
            });
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
