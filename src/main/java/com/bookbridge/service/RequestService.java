package com.bookbridge.service;

import com.bookbridge.dto.CreateRequestBody;
import com.bookbridge.dto.DeliveryUpdateRequest;
import com.bookbridge.dto.RequestResponse;
import com.bookbridge.entity.Book;
import com.bookbridge.entity.BorrowRequest;
import com.bookbridge.entity.User;
import com.bookbridge.exception.ApiException;
import com.bookbridge.repository.BookRepository;
import com.bookbridge.repository.BorrowRequestRepository;
import com.bookbridge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {
    private static final int BORROW_PERIOD_DAYS = 14;

    private final BorrowRequestRepository repo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public RequestService(BorrowRequestRepository repo, BookRepository bookRepo, UserRepository userRepo,
                           NotificationService notificationService, ActivityService activityService) {
        this.repo = repo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    /** Turns a raw BorrowRequest into the shape the frontend actually expects — with the book, owner and requester details filled in. */
    private RequestResponse enrich(BorrowRequest r) {
        RequestResponse dto = new RequestResponse();
        dto.id = r.getId();
        dto.bookId = r.getBookId();
        dto.book = bookRepo.findById(r.getBookId()).orElse(null);

        User requester = userRepo.findById(r.getRequesterId()).orElse(null);
        dto.requesterId = r.getRequesterId();
        dto.requesterName = requester != null ? requester.getName() : "Unknown";
        dto.requesterPhone = requester != null ? requester.getPhone() : "";
        dto.requesterEmail = requester != null ? requester.getEmail() : "";

        User owner = userRepo.findById(r.getOwnerId()).orElse(null);
        dto.ownerId = r.getOwnerId();
        dto.ownerName = owner != null ? owner.getName() : "Unknown";
        dto.ownerPhone = owner != null ? owner.getPhone() : "";
        dto.ownerEmail = owner != null ? owner.getEmail() : "";

        dto.status = r.getStatus();
        dto.createdAt = r.getCreatedAt();
        dto.dueDate = r.getDueDate();
        dto.respondedAt = r.getRespondedAt();
        dto.completedAt = r.getCompletedAt();
        dto.deliveryStatus = r.getDeliveryStatus();
        dto.deliveryPartner = r.getDeliveryPartner();
        dto.trackingId = r.getTrackingId();
        dto.adminNotes = r.getAdminNotes();
        dto.deliveryAddress = r.getDeliveryAddress();
        dto.paymentMethod = r.getPaymentMethod();
        dto.price = r.getPrice();
        dto.isPurchase = r.getIsPurchase();
        return dto;
    }

    public RequestResponse create(CreateRequestBody body, Long requesterId) {
        Book book = bookRepo.findById(body.bookId()).orElseThrow(() -> new ApiException("Book not found.", HttpStatus.NOT_FOUND));
        if (book.getOwnerId().equals(requesterId)) throw new ApiException("You can't request your own book.");
        boolean duplicate = !repo.findByBookIdAndRequesterIdAndStatus(book.getId(), requesterId, "Pending").isEmpty();
        if (duplicate) throw new ApiException("You've already requested this book.");

        BorrowRequest r = new BorrowRequest();
        r.setBookId(book.getId());
        r.setRequesterId(requesterId);
        r.setOwnerId(book.getOwnerId());
        r.setDeliveryAddress(body.address());
        r.setPaymentMethod(body.paymentMethod());
        boolean isPurchase = "Sell".equals(book.getListingType());
        r.setIsPurchase(isPurchase);
        r.setPrice(isPurchase ? book.getPrice() : null);
        repo.save(r);

        notificationService.push(book.getOwnerId(), "request",
            "New " + (isPurchase ? "purchase " : "") + "request for \"" + book.getTitle() + "\"", "requests.html");
        activityService.log("request_created", "Request #" + r.getId() + " created for \"" + book.getTitle() + "\""
            + (isPurchase ? " (Sale, ₹" + r.getPrice() + ")" : ""));
        return enrich(r);
    }

    public List<RequestResponse> mine(Long userId) { return repo.findByRequesterId(userId).stream().map(this::enrich).toList(); }
    public List<RequestResponse> incoming(Long userId) { return repo.findByOwnerId(userId).stream().map(this::enrich).toList(); }
    public List<RequestResponse> borrowed(Long userId) {
        return repo.findByRequesterIdAndStatusAndIsPurchase(userId, "Accepted", false).stream().map(this::enrich).toList();
    }

    public RequestResponse updateStatus(Long id, String status, Long callerId) {
        BorrowRequest r = repo.findById(id).orElseThrow(() -> new ApiException("Request not found.", HttpStatus.NOT_FOUND));
        if (!r.getOwnerId().equals(callerId)) throw new ApiException("Not authorized.", HttpStatus.FORBIDDEN);
        Book book = bookRepo.findById(r.getBookId()).orElse(null);

        r.setStatus(status);
        if ("Accepted".equals(status)) {
            r.setRespondedAt(LocalDateTime.now());
            r.setDueDate(LocalDateTime.now().plusDays(BORROW_PERIOD_DAYS));
            r.setDeliveryStatus("Awaiting Pickup");
            if (book != null && !r.getIsPurchase()) { book.setAvailable(false); bookRepo.save(book); }
            notificationService.push(r.getRequesterId(), "accepted",
                "Your request for \"" + (book != null ? book.getTitle() : "a book") + "\" was accepted!", "requests.html");
        } else if ("Rejected".equals(status)) {
            r.setRespondedAt(LocalDateTime.now());
            notificationService.push(r.getRequesterId(), "rejected",
                "Your request for \"" + (book != null ? book.getTitle() : "a book") + "\" was declined.", "requests.html");
        } else if ("Completed".equals(status)) {
            r.setCompletedAt(LocalDateTime.now());
            r.setDeliveryStatus("Delivered");
            if (book != null) {
                if (r.getIsPurchase()) bookRepo.delete(book);
                else { book.setAvailable(true); bookRepo.save(book); }
            }
            notificationService.push(r.getRequesterId(), "completed",
                "Exchange for \"" + (book != null ? book.getTitle() : "a book") + "\" is complete. Leave a review!",
                "book-details.html?id=" + r.getBookId());
        }
        repo.save(r);
        activityService.log("request_status", "Request #" + r.getId() + " ("
            + (book != null ? book.getTitle() : "?") + ") \u2192 " + status);
        return enrich(r);
    }

    public RequestResponse updateDelivery(Long id, DeliveryUpdateRequest body, Long callerId, boolean isAdmin) {
        BorrowRequest r = repo.findById(id).orElseThrow(() -> new ApiException("Request not found.", HttpStatus.NOT_FOUND));
        if (!isAdmin && !r.getOwnerId().equals(callerId)) throw new ApiException("Not authorized.", HttpStatus.FORBIDDEN);
        if (body.deliveryStatus() != null) r.setDeliveryStatus(body.deliveryStatus());
        if (body.deliveryPartner() != null) r.setDeliveryPartner(body.deliveryPartner());
        if (body.trackingId() != null) r.setTrackingId(body.trackingId());
        if (body.adminNotes() != null) r.setAdminNotes(body.adminNotes());
        repo.save(r);
        return enrich(r);
    }

    public List<RequestResponse> acceptedAndCompleted() {
        return repo.findByStatusIn(List.of("Accepted", "Completed")).stream().map(this::enrich).toList();
    }
}