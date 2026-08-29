package com.bookbridge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "events")
@Data
public class EventItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    private String location;

    @Column(nullable = false)
    private String type; // "Meetup" | "Offer"

   @Column(name = "event_desc", length = 1000)
   private String desc;
}
