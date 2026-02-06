package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    void deleteAllByPostId(Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    int countByPostId(Long postId);
}
