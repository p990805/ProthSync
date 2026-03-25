package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import com.prothsync.prothsync.repository.jpa.PostJpaRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return postJpaRepository.findById(postId);
    }

    @Override
    public void softDelete(Post post, Long userId) {
        post.delete(userId);
        postJpaRepository.save(post);
    }

    @Override
    public Page<Post> findAllByVisibilityPublic(Pageable pageable) {
        return postJpaRepository.findAllByVisibility(PostVisibility.PUBLIC, pageable);
    }

    @Override
    public Page<Post> findAllByUserId(Long userId, Pageable pageable) {
        return postJpaRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Post> findAllByCategory(PostCategory category, Pageable pageable) {
        return postJpaRepository.findAllByCategory(category, pageable);
    }

    @Override
    public Page<Post> findAllByUserIdAndVisibilityPublic(Long userId, Pageable pageable) {
        return postJpaRepository.findAllByUserIdAndVisibility(userId, PostVisibility.PUBLIC, pageable);
    }

    @Override
    public Page<Post> findFeedPostsByUserIds(List<Long> userIds, Pageable pageable) {
        return postJpaRepository.findAllByUserIdInAndVisibility(userIds, PostVisibility.PUBLIC, pageable);
    }

    @Override
    public Page<Post> findAllByHashtagIdAndVisibilityPublic(Long hashtagId, Pageable pageable) {
        return postJpaRepository.findAllByHashtagIdAndVisibilityPublic(hashtagId, pageable);
    }

    @Override
    public long count() {
        return postJpaRepository.count();
    }

    @Override
    public List<Post> findAllByIds(List<Long> postIds) {
        return postJpaRepository.findAllByPostIdIn(postIds);
    }

    @Override
    public void incrementViewCount(Long postId, int delta){
        postJpaRepository.incrementViewCount(postId,delta);
    }

    @Override
    public void incrementLikeCount(Long postId){
        postJpaRepository.incrementLikeCount(postId);
    }

    @Override
    public void decrementLikeCount(Long postId){
        postJpaRepository.decrementLikeCount(postId);
    }
}