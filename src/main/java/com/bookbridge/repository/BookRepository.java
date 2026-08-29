package com.bookbridge.repository;

import com.bookbridge.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByOwnerId(Long ownerId);
    List<Book> findByCategory(String category);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    long countByCategory(String category);
}
