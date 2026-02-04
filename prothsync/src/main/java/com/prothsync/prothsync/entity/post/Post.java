package com.prothsync.prothsync.entity.post;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    private static final int MAX_CONTENT_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 2000)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostVisibility visibility;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Column(nullable = false)
    private int viewCount = 0;

    private Post(Long userId, String content, PostCategory category, PostVisibility visibility) {
        this.userId = userId;
        this.content = content;
        this.category = category;
        this.visibility = visibility;
    }

    public static Post create(Long userId, String content, PostCategory category, PostVisibility visibility) {
        validateUserId(userId);
        validateContent(content);
        validateCategory(category);
        validateVisibility(visibility);

        return new Post(userId, content, category, visibility);
    }

    private static void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(PostErrorCode.POST_USER_ID_NULL);
        }
    }

    private static void validateContent(String content) {
        if (content != null && content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(PostErrorCode.POST_CONTENT_TOO_LONG);
        }
    }

    private static void validateCategory(PostCategory category) {
        if (category == null) {
            throw new BusinessException(PostErrorCode.POST_CATEGORY_NULL);
        }
    }

    private static void validateVisibility(PostVisibility visibility) {
        if (visibility == null) {
            throw new BusinessException(PostErrorCode.POST_VISIBILITY_NULL);
        }
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void updateCategory(PostCategory category) {
        validateCategory(category);
        this.category = category;
    }

    public void updateVisibility(PostVisibility visibility) {
        validateVisibility(visibility);
        this.visibility = visibility;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public boolean isOwner(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isPublic() {
        return this.visibility == PostVisibility.PUBLIC;
    }
}