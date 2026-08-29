package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String status = "Pending"; // Pending | Accepted | Rejected | Completed

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime dueDate;
    private LocalDateTime respondedAt;
    private LocalDateTime completedAt;

    private String deliveryStatus = "Not Started";
    private String deliveryPartner;
    private String trackingId;
    @Column(length = 1000)
    private String adminNotes;

    // Sell-flow fields
    @Column(length = 1000)
    private String deliveryAddress;
    private String paymentMethod;
    private Double price;
    @Column(nullable = false)
    private Boolean isPurchase = false;
}
