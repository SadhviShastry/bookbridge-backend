package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String type; // request | accepted | rejected | completed | review | message

    @Column(nullable = false)
    private String text;

    private String link;

    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
