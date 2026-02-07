package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.Comment;
import java.time.LocalDateTime;

public record CommentResponseDTO(
    Long commentId,
    Long postId,
    Long userId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static CommentResponseDTO from(Comment comment) {
        return new CommentResponseDTO(
            comment.getCommentId(),
            comment.getPostId(),
            comment.getUserId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }
}
