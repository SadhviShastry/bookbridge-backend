package com.bookbridge.dto;

public record AuthRequest(String email, String password, String role) {}
