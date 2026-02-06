package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.PostLike;
import java.util.Optional;

public interface PostLikeRepository {

    PostLike save(PostLike postLike);

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    void delete(PostLike postLike);

    void deleteAllByPostId(Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    int countByPostId(Long postId);
}
