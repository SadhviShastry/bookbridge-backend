package com.bookbridge.repository;

import com.bookbridge.entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {
    List<BorrowRequest> findByRequesterId(Long requesterId);
    List<BorrowRequest> findByOwnerId(Long ownerId);
    List<BorrowRequest> findByBookIdAndRequesterIdAndStatus(Long bookId, Long requesterId, String status);
    List<BorrowRequest> findByStatusIn(List<String> statuses);
    List<BorrowRequest> findByRequesterIdAndStatusAndIsPurchase(Long requesterId, String status, Boolean isPurchase);
    long countByStatus(String status);
}
