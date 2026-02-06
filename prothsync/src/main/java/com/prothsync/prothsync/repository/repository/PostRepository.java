package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

    Post save(Post post);
    Optional<Post> findById(Long postId);
    void delete(Post post);
    Page<Post> findAllByVisibilityPublic(Pageable pageable);

    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    Page<Post> findAllByCategory(PostCategory category, Pageable pageable);

    Page<Post> findAllByUserIdAndVisibilityPublic(Long userId, Pageable pageable);


}
