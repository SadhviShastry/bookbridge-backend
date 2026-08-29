package com.bookbridge.dto;

public record DeliveryUpdateRequest(String deliveryStatus, String deliveryPartner, String trackingId, String adminNotes) {}
