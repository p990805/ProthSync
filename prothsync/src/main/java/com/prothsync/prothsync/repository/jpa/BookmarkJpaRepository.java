package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkJpaRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Page<Bookmark> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteAllByPostId(Long postId);

    @Query("SELECT b.postId FROM Bookmark b WHERE b.userId = :userId AND b.postId IN :postIds")
    List<Long> findPostIdsByUserIdAndPostIdIn(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);
}