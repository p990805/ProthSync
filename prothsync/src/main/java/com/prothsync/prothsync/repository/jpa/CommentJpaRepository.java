package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    void deleteAllByPostId(Long postId);

    int countByPostId(Long postId);
}
