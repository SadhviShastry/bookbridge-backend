package com.bookbridge.repository;

import com.bookbridge.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventItemRepository extends JpaRepository<EventItem, Long> {
    List<EventItem> findAllByOrderByDateAsc();
}
