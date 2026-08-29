package com.bookbridge.dto;

public record CreateRequestBody(Long bookId, String address, String paymentMethod) {}
