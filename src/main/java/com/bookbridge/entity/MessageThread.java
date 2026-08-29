package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_threads")
@Data
public class MessageThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userAId;

    @Column(nullable = false)
    private Long userBId;

    private Long bookId;

    private String lastMessage;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
