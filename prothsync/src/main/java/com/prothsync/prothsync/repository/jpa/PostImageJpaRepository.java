package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostImageJpaRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostId(Long postId);

    List<PostImage> findAllByPostIdOrderByDisplayOrderAsc(Long postId);

    int countByPostId(Long postId);

    @Modifying
    @Query("UPDATE PostImage pi SET pi.deletedAt = CURRENT_TIMESTAMP, pi.deletedBy = :userId WHERE pi.postId = :postId AND pi.deletedAt IS NULL")
    int softDeleteAllByPostId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT pi FROM PostImage pi WHERE pi.postId IN :postIds " +
        "AND pi.displayOrder = (SELECT MIN(pi2.displayOrder) FROM PostImage pi2 WHERE pi2.postId = pi.postId)")
    List<PostImage> findFirstImagesByPostIds(@Param("postIds") List<Long> postIds);

    List<PostImage> findAllByPostIdInOrderByDisplayOrderAsc(List<Long> postIds);
}