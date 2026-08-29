package com.bookbridge.dto;

public record RegisterRequest(String fullName, String email, String phone, String area, String password) {}
