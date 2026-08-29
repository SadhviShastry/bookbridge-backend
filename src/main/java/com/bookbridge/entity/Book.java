package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String category;
    private String subcategory;

    @Column(name = "book_condition")
    private String condition; // New | Like New | Good | Fair

    @Column(name = "description", length = 2000)
    private String desc;

    @Column(length = 500000)
    private String imageUrl; // base64 data URL or a real uploaded file URL

    @Column(nullable = false)
    private String listingType = "Lend / Borrow"; // Lend / Borrow | Exchange | Donate | Sell

    private Double price; // only used when listingType == "Sell"

    private Double rating = 0.0;
    private Integer reviews = 0;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(nullable = false)
    private Long ownerId;

    private String owner; // denormalized owner name, refreshed on read in the service layer

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
