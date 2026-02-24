package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkJpaRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Page<Bookmark> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteAllByPostId(Long postId);
}