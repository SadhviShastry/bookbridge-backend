package com.bookbridge.repository;

import com.bookbridge.entity.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {
    List<MessageThread> findByUserAIdOrUserBId(Long userAId, Long userBId);
    Optional<MessageThread> findByBookIdAndUserAIdAndUserBId(Long bookId, Long userAId, Long userBId);
}
