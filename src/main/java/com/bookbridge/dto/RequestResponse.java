package com.bookbridge.dto;

import com.bookbridge.entity.Book;
import java.time.LocalDateTime;

public class RequestResponse {
    public Long id;
    public Long bookId;
    public Book book;
    public Long requesterId;
    public String requesterName;
    public String requesterPhone;
    public String requesterEmail;
    public Long ownerId;
    public String ownerName;
    public String ownerPhone;
    public String ownerEmail;
    public String status;
    public LocalDateTime createdAt;
    public LocalDateTime dueDate;
    public LocalDateTime respondedAt;
    public LocalDateTime completedAt;
    public String deliveryStatus;
    public String deliveryPartner;
    public String trackingId;
    public String adminNotes;
    public String deliveryAddress;
    public String paymentMethod;
    public Double price;
    public Boolean isPurchase;
}