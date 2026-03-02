package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    int countByPostId(Long postId);

    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = CURRENT_TIMESTAMP, c.deletedBy = :userId WHERE c.postId = :postId AND c.deletedAt IS NULL")
    int softDeleteAllByPostId(@Param("postId") Long postId, @Param("userId") Long userId);
}