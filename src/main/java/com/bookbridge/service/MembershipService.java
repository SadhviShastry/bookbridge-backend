package com.bookbridge.service;

import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MembershipService {
    private static final List<Map<String, Object>> TIERS = List.of(
        Map.of("id", "reader", "name", "Reader", "price", 0,
            "perks", List.of("Borrow up to 2 books at a time", "Standard support")),
        Map.of("id", "plus", "name", "BookBridge Plus", "price", 149,
            "perks", List.of("Borrow up to 6 books at a time", "Priority requests", "Early access to events")),
        Map.of("id", "patron", "name", "Community Patron", "price", 349,
            "perks", List.of("Unlimited borrowing", "Patron badge", "Vote on new features", "Invite to meetups"))
    );

    private final UserRepository userRepo;
    private final ActivityService activityService;

    public MembershipService(UserRepository userRepo, ActivityService activityService) {
        this.userRepo = userRepo;
        this.activityService = activityService;
    }

    public List<Map<String, Object>> tiers() { return TIERS; }

    public User join(Long userId, String tierId) {
        boolean validTier = TIERS.stream().anyMatch(t -> t.get("id").equals(tierId));
        if (!validTier) throw new ApiException("Membership tier not found.");
        User u = userRepo.findById(userId).orElseThrow(() -> new ApiException("User not found.", HttpStatus.NOT_FOUND));
        u.setMembership(tierId);
        userRepo.save(u);
        activityService.log("membership", u.getName() + " joined the \"" + tierId + "\" membership tier");
        return u;
    }
}
