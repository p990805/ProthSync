package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepository {

    Bookmark save(Bookmark bookmark);

    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);

    void delete(Bookmark bookmark);

    void deleteAllByPostId(Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Page<Bookmark> findAllByUserId(Long userId, Pageable pageable);
}