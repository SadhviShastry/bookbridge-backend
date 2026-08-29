package com.bookbridge.service;

import com.bookbridge.entity.Donation;
import com.bookbridge.entity.User;
import com.bookbridge.repository.DonationRepository;
import com.bookbridge.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DonationService {
    private final DonationRepository repo;
    private final UserRepository userRepo;
    private final ActivityService activityService;

    public DonationService(DonationRepository repo, UserRepository userRepo, ActivityService activityService) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.activityService = activityService;
    }

    public Donation create(Long userId, Double amount, String message) {
        Donation d = new Donation();
        d.setUserId(userId);
        d.setUserName(userId != null ? userRepo.findById(userId).map(User::getName).orElse("Anonymous") : "Anonymous");
        d.setAmount(amount == null ? 0 : amount);
        d.setMessage(message);
        repo.save(d);
        activityService.log("donation", d.getUserName() + " donated \u20b9" + d.getAmount()
            + (message != null && !message.isBlank() ? " \u2014 \"" + message + "\"" : ""));
        return d;
    }

    public List<Donation> list() { return repo.findAllByOrderByCreatedAtDesc(); }
}
