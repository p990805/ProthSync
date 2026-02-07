package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.post.PostCategory;
import java.time.LocalDateTime;

public record PostSummaryResponseDTO(
    Long postId,
    Long userId,
    String content,
    PostCategory category,
    String thumbnailUrl,
    int likeCount,
    int commentCount,
    int viewCount,
    LocalDateTime createdAt
) {

    private static final int SUMMARY_MAX_LENGTH = 100;

    public static PostSummaryResponseDTO of(Post post, String thumbnailUrl) {
        return new PostSummaryResponseDTO(
            post.getPostId(),
            post.getUserId(),
            truncateContent(post.getContent()),
            post.getCategory(),
            thumbnailUrl,
            post.getLikeCount(),
            post.getCommentCount(),
            post.getViewCount(),
            post.getCreatedAt()
        );
    }

    private static String truncateContent(String content) {
        if (content == null) {
            return null;
        }
        if (content.length() <= SUMMARY_MAX_LENGTH) {
            return content;
        }
        return content.substring(0, SUMMARY_MAX_LENGTH) + "...";
    }
}
