package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.Comment;
import com.prothsync.prothsync.repository.jpa.CommentJpaRepository;
import com.prothsync.prothsync.repository.repository.CommentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        return commentJpaRepository.findById(commentId);
    }

    @Override
    public Page<Comment> findAllByPostId(Long postId, Pageable pageable) {
        return commentJpaRepository.findAllByPostId(postId, pageable);
    }

    @Override
    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        commentJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public int countByPostId(Long postId) {
        return commentJpaRepository.countByPostId(postId);
    }
}
