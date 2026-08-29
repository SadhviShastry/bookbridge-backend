package com.bookbridge.controller;

import com.bookbridge.dto.CreateRequestBody;
import com.bookbridge.dto.DeliveryUpdateRequest;
import com.bookbridge.entity.BorrowRequest;
import com.bookbridge.security.CurrentUser;
import com.bookbridge.service.RequestService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    private final RequestService requestService;
    public RequestController(RequestService requestService) { this.requestService = requestService; }

    @PostMapping
    public BorrowRequest create(@RequestBody CreateRequestBody body) { return requestService.create(body, CurrentUser.id()); }

    @GetMapping("/mine")
    public List<BorrowRequest> mine() { return requestService.mine(CurrentUser.id()); }

    @GetMapping("/incoming")
    public List<BorrowRequest> incoming() { return requestService.incoming(CurrentUser.id()); }

    @GetMapping("/borrowed")
    public List<BorrowRequest> borrowed() { return requestService.borrowed(CurrentUser.id()); }

    @PutMapping("/{id}/accept")
    public BorrowRequest accept(@PathVariable Long id) { return requestService.updateStatus(id, "Accepted", CurrentUser.id()); }

    @PutMapping("/{id}/reject")
    public BorrowRequest reject(@PathVariable Long id) { return requestService.updateStatus(id, "Rejected", CurrentUser.id()); }

    @PutMapping("/{id}/complete")
    public BorrowRequest complete(@PathVariable Long id) { return requestService.updateStatus(id, "Completed", CurrentUser.id()); }

    @PutMapping("/{id}/delivery")
    public BorrowRequest delivery(@PathVariable Long id, @RequestBody DeliveryUpdateRequest body) {
        return requestService.updateDelivery(id, body, CurrentUser.id(), CurrentUser.isAdmin());
    }
}
