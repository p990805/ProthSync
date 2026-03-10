package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.Bookmark;
import com.prothsync.prothsync.repository.jpa.BookmarkJpaRepository;
import com.prothsync.prothsync.repository.repository.BookmarkRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepository {

    private final BookmarkJpaRepository bookmarkJpaRepository;

    @Override
    public Bookmark save(Bookmark bookmark) {
        return bookmarkJpaRepository.save(bookmark);
    }

    @Override
    public Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId) {
        return bookmarkJpaRepository.findByUserIdAndPostId(userId, postId);
    }

    @Override
    public void delete(Bookmark bookmark) {
        bookmarkJpaRepository.delete(bookmark);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        bookmarkJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return bookmarkJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public Page<Bookmark> findAllByUserId(Long userId, Pageable pageable) {
        return bookmarkJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public List<Long> findBookmarkedPostIds(Long userId, List<Long> postIds) {
        return bookmarkJpaRepository.findPostIdsByUserIdAndPostIdIn(userId, postIds);
    }
}