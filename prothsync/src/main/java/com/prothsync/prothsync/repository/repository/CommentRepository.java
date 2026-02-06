package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long commentId);

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    void delete(Comment comment);

    void deleteAllByPostId(Long postId);

    int countByPostId(Long postId);
}
