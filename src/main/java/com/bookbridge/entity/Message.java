package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long threadId;

    @Column(nullable = false)
    private Long senderId;

    @Column(length = 2000, nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();
}
