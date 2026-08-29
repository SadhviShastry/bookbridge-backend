package com.bookbridge.service;

import com.bookbridge.dto.*;
import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.UserRepository;
import com.bookbridge.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ActivityService activityService;

    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                        UserService userService, ActivityService activityService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.activityService = activityService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (repo.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException("An account with this email already exists.");
        }
        User u = new User();
        u.setName(req.fullName());
        u.setEmail(req.email());
        u.setPhone(req.phone());
        u.setArea(req.area());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole("user");
        u.setInitials(initialsOf(req.fullName()));
        repo.save(u);
        activityService.log("user_registered", "New user registered: " + u.getName() + " (" + u.getEmail() + ")");
        String token = jwtUtil.generateToken(u.getId(), u.getRole());
        return new AuthResponse(token, userService.sanitize(u));
    }

    public AuthResponse login(AuthRequest req) {
        User u = repo.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new ApiException("Invalid email or password.", HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(req.password(), u.getPassword())) {
            throw new ApiException("Invalid email or password.", HttpStatus.UNAUTHORIZED);
        }
        if (req.role() != null && !req.role().isBlank() && !u.getRole().equals(req.role())) {
            throw new ApiException(u.getRole().equals("admin")
                ? "Please use the Admin Login tab for this account."
                : "This account doesn't have admin access.");
        }
        String token = jwtUtil.generateToken(u.getId(), u.getRole());
        return new AuthResponse(token, userService.sanitize(u));
    }

    public void forgotPassword(String email) {
        repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException("No account found with that email.", HttpStatus.NOT_FOUND));
        // In production: generate a reset token and email it via JavaMailSender.
        // Wire this up once you add an email provider (see deployment guide).
    }

    private String initialsOf(String name) {
        if (name == null || name.isBlank()) return "U";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        return sb.toString();
    }
}
