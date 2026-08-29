package com.bookbridge.config;

import com.bookbridge.entity.*;
import com.bookbridge.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Runs once on startup — seeds an admin account and a few categories/events
 *  if the database is empty, so the app is usable immediately after deploy. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final EventItemRepository eventRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepo, CategoryRepository categoryRepo,
                       EventItemRepository eventRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.eventRepo = eventRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@bookbridge.com");
            admin.setPhone("9999999999");
            admin.setPassword(passwordEncoder.encode("admin123")); // CHANGE after first login in production
            admin.setRole("admin");
            admin.setInitials("AD");
            userRepo.save(admin);
        }

        if (categoryRepo.count() == 0) {
            String[][] cats = {
                {"Programming", "fa-code"}, {"Novels", "fa-feather"}, {"Business", "fa-briefcase"},
                {"Academic", "fa-graduation-cap"}, {"Self Help", "fa-seedling"}, {"Competitive Exams", "fa-layer-group"}
            };
            for (String[] c : cats) {
                Category cat = new Category();
                cat.setName(c[0]);
                cat.setIcon(c[1]);
                categoryRepo.save(cat);
            }
        }

        if (eventRepo.count() == 0) {
            EventItem e1 = new EventItem();
            e1.setTitle("Monsoon Reads Book Swap");
            e1.setDate(LocalDate.of(2026, 9, 14));
            e1.setLocation("Andheri Community Hall");
            e1.setType("Meetup");
            e1.setDesc("Bring 2 books, take 2 books — casual meetup with chai and book talk.");
            eventRepo.save(e1);

            EventItem e2 = new EventItem();
            e2.setTitle("September Donation Drive");
            e2.setDate(LocalDate.of(2026, 9, 20));
            e2.setLocation("Online");
            e2.setType("Offer");
            e2.setDesc("Donate 3+ books this month and unlock a bonus badge.");
            eventRepo.save(e2);
        }
    }
}
