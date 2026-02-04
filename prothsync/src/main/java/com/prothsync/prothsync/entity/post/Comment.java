package com.prothsync.prothsync.entity.post;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments" )
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    private static final int MAX_CONTENT_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String content;

    private Comment(Long postId, Long userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public static Comment createComment(Long postId, Long userId, String content) {
        validatePostId(postId);
        validateUserId(userId);
        validateContent(content);

        return new Comment(postId, userId, content);
    }

    private static void validatePostId(Long postId) {
        if (postId == null) {
            throw new BusinessException(PostErrorCode.POST_ID_NULL);
        }
    }

    private static void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(PostErrorCode.POST_USER_ID_NULL);
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(PostErrorCode.POST_CONTENT_NULL);
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(PostErrorCode.COMMENT_CONTENT_TOO_LONG);
        }
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

}
