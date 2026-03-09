package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByVisibility(PostVisibility visibility, Pageable pageable);

    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    Page<Post> findAllByCategory(PostCategory category, Pageable pageable);

    Page<Post> findAllByUserIdAndVisibility(Long userId, PostVisibility visibility, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.userId IN :userIds AND p.visibility = :visibility ORDER BY p.createdAt DESC")
    Page<Post> findAllByUserIdInAndVisibility(
        @Param("userIds") List<Long> userIds,
        @Param("visibility") PostVisibility visibility,
        Pageable pageable
    );

    @Query("""
        SELECT p FROM Post p
        WHERE p.postId IN (
            SELECT ph.postId FROM PostHashtag ph WHERE ph.hashtagId = :hashtagId
        ) AND p.visibility = 'PUBLIC'
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findAllByHashtagIdAndVisibilityPublic(
        @Param("hashtagId") Long hashtagId, Pageable pageable);

    List<Post> findAllByPostIdIn(List<Long> postIds);
}