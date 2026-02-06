package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import com.prothsync.prothsync.repository.jpa.PostJpaRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
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
    public void delete(Post post) {
        postJpaRepository.delete(post);
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


}
