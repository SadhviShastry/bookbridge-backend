package com.bookbridge.dto;

import com.bookbridge.entity.Book;
import com.bookbridge.entity.User;
import java.time.LocalDateTime;

public class ThreadResponse {
    public Long id;
    public Long bookId;
    public Book book;
    public User otherUser;
    public String lastMessage;
    public LocalDateTime updatedAt;
}