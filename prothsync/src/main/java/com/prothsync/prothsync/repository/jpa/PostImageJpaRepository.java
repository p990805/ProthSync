package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.post.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageJpaRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostId(Long postId);

    List<PostImage> findAllByPostIdOrderByDisplayOrderAsc(Long postId);

    void deleteAllByPostId(Long postId);

    int countByPostId(Long postId);
}
