package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDTO(
    Long postId,
    Long userId,
    String content,
    PostCategory category,
    PostVisibility visibility,
    List<PostImageResponseDTO> images,
    List<HashtagResponseDTO> hashtags,
    int likeCount,
    int commentCount,
    int viewCount,
    boolean isLiked,
    boolean isBookmarked,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static PostResponseDTO of(
        Post post,
        List<PostImageResponseDTO> images,
        List<HashtagResponseDTO> hashtags,
        boolean isLiked,
        boolean isBookmarked
    ) {
        return new PostResponseDTO(
            post.getPostId(),
            post.getUserId(),
            post.getContent(),
            post.getCategory(),
            post.getVisibility(),
            images,
            hashtags,
            post.getLikeCount(),
            post.getCommentCount(),
            post.getViewCount(),
            isLiked,
            isBookmarked,
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }

    public static PostResponseDTO ofWithViewCount(
        Post post,
        List<PostImageResponseDTO> images,
        List<HashtagResponseDTO> hashtags,
        boolean isLiked,
        boolean isBookmarked,
        int viewCount
    ) {
        return new PostResponseDTO(
            post.getPostId(),
            post.getUserId(),
            post.getContent(),
            post.getCategory(),
            post.getVisibility(),
            images,
            hashtags,
            post.getLikeCount(),
            post.getCommentCount(),
            viewCount,
            isLiked,
            isBookmarked,
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}