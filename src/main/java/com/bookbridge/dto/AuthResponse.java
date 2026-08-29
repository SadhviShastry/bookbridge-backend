package com.bookbridge.dto;

import com.bookbridge.entity.User;

public record AuthResponse(String token, User user) {}
