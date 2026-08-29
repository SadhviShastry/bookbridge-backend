package com.bookbridge.dto;

public record BookRequest(
    String title, String author, String category, String subcategory,
    String condition, String listingType, String price, String desc, String imageUrl
) {}
