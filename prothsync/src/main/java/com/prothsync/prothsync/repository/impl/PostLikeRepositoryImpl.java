package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.PostLike;
import com.prothsync.prothsync.repository.jpa.PostLikeJpaRepository;
import com.prothsync.prothsync.repository.repository.PostLikeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepository {

    private final PostLikeJpaRepository postLikeJpaRepository;

    @Override
    public PostLike save(PostLike postLike) {
        return postLikeJpaRepository.save(postLike);
    }

    @Override
    public Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId) {
        return postLikeJpaRepository.findByUserIdAndPostId(userId, postId);
    }

    @Override
    public void delete(PostLike postLike) {
        postLikeJpaRepository.delete(postLike);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        postLikeJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return postLikeJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public int countByPostId(Long postId) {
        return postLikeJpaRepository.countByPostId(postId);
    }

    @Override
    public List<Long> findLikedPostIds(Long userId, List<Long> postIds) {
        return postLikeJpaRepository.findPostIdsByUserIdAndPostIdIn(userId, postIds);
    }
}
