package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

    Post save(Post post);
    Optional<Post> findById(Long postId);
    void softDelete(Post post, Long userId);
    Page<Post> findAllByVisibilityPublic(Pageable pageable);

    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    Page<Post> findAllByCategory(PostCategory category, Pageable pageable);

    Page<Post> findAllByUserIdAndVisibilityPublic(Long userId, Pageable pageable);

    Page<Post> findFeedPostsByUserIds(List<Long> userIds, Pageable pageable);

    Page<Post> findAllByHashtagIdAndVisibilityPublic(Long hashtagId, Pageable pageable);

    long count();

    List<Post> findAllByIds(List<Long> postIds);

    void incrementViewCount(Long postId, int delta);

    void incrementLikeCount(Long postId);
    void decrementLikeCount(Long postId);

    void incrementCommentCount(Long postId);
    void decrementCommentCount(Long postId);
}