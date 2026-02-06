package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.post.PostImage;
import com.prothsync.prothsync.repository.jpa.PostImageJpaRepository;
import com.prothsync.prothsync.repository.repository.PostImageRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostImageRepositoryImpl implements PostImageRepository {

    private final PostImageJpaRepository postImageJpaRepository;

    @Override
    public PostImage save(PostImage postImage) {
        return postImageJpaRepository.save(postImage);
    }

    @Override
    public List<PostImage> saveAll(List<PostImage> postImages) {
        return postImageJpaRepository.saveAll(postImages);
    }

    @Override
    public Optional<PostImage> findById(Long postImageId) {
        return postImageJpaRepository.findById(postImageId);
    }

    @Override
    public List<PostImage> findAllByPostId(Long postId) {
        return postImageJpaRepository.findAllByPostId(postId);
    }

    @Override
    public List<PostImage> findAllByPostIdOrderByDisplayOrder(Long postId) {
        return postImageJpaRepository.findAllByPostIdOrderByDisplayOrderAsc(postId);
    }

    @Override
    public void delete(PostImage postImage) {
        postImageJpaRepository.delete(postImage);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        postImageJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public int countByPostId(Long postId) {
        return postImageJpaRepository.countByPostId(postId);
    }
}
