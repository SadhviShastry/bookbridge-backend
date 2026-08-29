package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password; // BCrypt-hashed, never returned to the client

    @Column(name = "user_role", nullable = false)
    private String role = "user"; // "user" | "admin"

    private String initials;
    private String area;
    private String bio;

    /** Membership tier id: "reader" | "plus" | "patron" (null = default/free). */
    private String membership;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
