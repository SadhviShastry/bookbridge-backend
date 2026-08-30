package com.bookbridge.controller;

import com.bookbridge.dto.CreateRequestBody;
import com.bookbridge.dto.DeliveryUpdateRequest;
import com.bookbridge.dto.RequestResponse;
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
    public RequestResponse create(@RequestBody CreateRequestBody body) { return requestService.create(body, CurrentUser.id()); }

    @GetMapping("/mine")
    public List<RequestResponse> mine() { return requestService.mine(CurrentUser.id()); }

    @GetMapping("/incoming")
    public List<RequestResponse> incoming() { return requestService.incoming(CurrentUser.id()); }

    @GetMapping("/borrowed")
    public List<RequestResponse> borrowed() { return requestService.borrowed(CurrentUser.id()); }

    @PutMapping("/{id}/accept")
    public RequestResponse accept(@PathVariable Long id) { return requestService.updateStatus(id, "Accepted", CurrentUser.id()); }

    @PutMapping("/{id}/reject")
    public RequestResponse reject(@PathVariable Long id) { return requestService.updateStatus(id, "Rejected", CurrentUser.id()); }

    @PutMapping("/{id}/complete")
    public RequestResponse complete(@PathVariable Long id) { return requestService.updateStatus(id, "Completed", CurrentUser.id()); }

    @PutMapping("/{id}/delivery")
    public RequestResponse delivery(@PathVariable Long id, @RequestBody DeliveryUpdateRequest body) {
        return requestService.updateDelivery(id, body, CurrentUser.id(), CurrentUser.isAdmin());
    }
}