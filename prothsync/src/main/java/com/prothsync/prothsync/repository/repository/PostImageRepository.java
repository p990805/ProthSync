package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.PostImage;
import java.util.List;
import java.util.Optional;

public interface PostImageRepository {

    PostImage save(PostImage postImage);

    List<PostImage> saveAll(List<PostImage> postImages);

    Optional<PostImage> findById(Long postImageId);

    List<PostImage> findAllByPostId(Long postId);

    List<PostImage> findAllByPostIdOrderByDisplayOrder(Long postId);

    void delete(PostImage postImage);

    void softDeleteAllByPostId(Long postId, Long userId);

    int countByPostId(Long postId);

    List<PostImage> findFirstImagesByPostIds(List<Long> postIds);

    List<PostImage> findAllByPostIdsOrderByDisplayOrder(List<Long> postIds);
}