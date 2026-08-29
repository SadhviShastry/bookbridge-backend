package com.bookbridge.config;

import com.bookbridge.entity.*;
import com.bookbridge.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Runs once on startup — seeds an admin account, categories, events, and
 *  demo books if the database is empty, so the app is usable immediately
 *  after deploy. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final EventItemRepository eventRepo;
    private final BookRepository bookRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepo, CategoryRepository categoryRepo,
                       EventItemRepository eventRepo, BookRepository bookRepo,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.eventRepo = eventRepo;
        this.bookRepo = bookRepo;
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

        if (bookRepo.count() == 0) {
            User owner = userRepo.findByEmailIgnoreCase("admin@bookbridge.com").orElse(null);
            if (owner != null) {
                String[][] books = {
                    {"Atomic Habits", "James Clear", "Self Help", "Like New", "Tiny changes, remarkable results.", "assets/images.jpg"},
                    {"The Alchemist", "Paulo Coelho", "Novels", "Good", "A shepherd boy's journey to fulfil his personal legend.", "assets/download.jpg"},
                    {"Rich Dad Poor Dad", "Robert Kiyosaki", "Business", "Good", "What the rich teach their kids about money.", "assets/richdadpoordad.jpg"},
                    {"The 5 AM Club", "Robin Sharma", "Self Help", "New", "Own your morning, elevate your life.", "assets/5amclub.jpg"},
                    {"Sapiens", "Yuval Noah Harari", "Academic", "Like New", "A brief history of humankind.", "assets/sapiens.png"},
                    {"Harry Potter", "J.K. Rowling", "Novels", "Good", "The boy who lived begins his journey at Hogwarts.", "assets/Harrypotter.jpg"},
                    {"Clean Code", "Robert C. Martin", "Programming", "Good", "A handbook of agile software craftsmanship.", "assets/cleancode.jpg"},
                    {"The Pragmatic Programmer", "Andrew Hunt", "Programming", "Like New", "Your journey to mastery.", "assets/pragmaticprogrammer.jpg"},
                    {"Thinking, Fast and Slow", "Daniel Kahneman", "Academic", "Good", "How two systems drive the way we think.", "assets/thinkingfastandslow.jpg"},
                    {"You Don't Know JS", "Kyle Simpson", "Programming", "Good", "A deep dive into JavaScript.", "assets/youdontknowjs.jpg"},
                    {"Deep Work", "Cal Newport", "Self Help", "New", "Rules for focused success.", "assets/deepwork.jpg"},
                    {"The Power of Your Subconscious Mind", "Joseph Murphy", "Self Help", "Good", "Classic guide to using the subconscious.", "assets/powerofsubconisiousmind.jpg"},
                    {"How to Win Friends & Influence People", "Dale Carnegie", "Business", "Like New", "Timeless principles for relationships.", "assets/howtowinfriends.jpg"},
                    {"Educated", "Tara Westover", "Academic", "Good", "A memoir about transformative education.", "assets/educated.jpg"},
                    {"The Power of Now", "Eckhart Tolle", "Self Help", "New", "A guide to spiritual enlightenment.", "assets/poweofnow.jpg"}
                };
                for (String[] b : books) {
                    Book book = new Book();
                    book.setTitle(b[0]);
                    book.setAuthor(b[1]);
                    book.setCategory(b[2]);
                    book.setCondition(b[3]);
                    book.setDesc(b[4]);
                    book.setImageUrl(b[5]);
                    book.setListingType("Lend / Borrow");
                    book.setAvailable(true);
                    book.setOwnerId(owner.getId());
                    book.setOwner(owner.getName());
                    bookRepo.save(book);
                }
            }
        }
    }
}