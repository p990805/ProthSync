package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByVisibility(PostVisibility visibility, Pageable pageable);

    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    Page<Post> findAllByCategory(PostCategory category, Pageable pageable);

    Page<Post> findAllByUserIdAndVisibility(Long userId, PostVisibility visibility, Pageable pageable);

}
