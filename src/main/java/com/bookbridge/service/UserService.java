package com.bookbridge.service;

import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final ActivityService activityService;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder, ActivityService activityService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.activityService = activityService;
    }

    /** Never expose the password hash to the client. */
    public User sanitize(User u) {
        if (u == null) return null;
        User copy = new User();
        copy.setId(u.getId()); copy.setName(u.getName()); copy.setEmail(u.getEmail());
        copy.setPhone(u.getPhone()); copy.setRole(u.getRole()); copy.setInitials(u.getInitials());
        copy.setArea(u.getArea()); copy.setBio(u.getBio()); copy.setMembership(u.getMembership());
        copy.setCreatedAt(u.getCreatedAt());
        copy.setPassword(null);
        return copy;
    }

    public User getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ApiException("User not found.", HttpStatus.NOT_FOUND));
    }

    public User update(Long id, User patch) {
        User u = getById(id);
        if (patch.getName() != null) u.setName(patch.getName());
        if (patch.getEmail() != null) u.setEmail(patch.getEmail());
        if (patch.getPhone() != null) u.setPhone(patch.getPhone());
        if (patch.getArea() != null) u.setArea(patch.getArea());
        if (patch.getBio() != null) u.setBio(patch.getBio());
        return repo.save(u);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        User u = getById(id);
        if (!passwordEncoder.matches(oldPassword, u.getPassword())) {
            throw new ApiException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new ApiException("New password must be at least 6 characters.");
        }
        u.setPassword(passwordEncoder.encode(newPassword));
        repo.save(u);
    }

    public void delete(Long id) {
        repo.deleteById(id);
        activityService.log("user_deleted", "User #" + id + " deleted their account");
    }
}
