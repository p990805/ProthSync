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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "post_likes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_post_likes_user_post",
            columnNames = {"user_id","post_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    private PostLike(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public static PostLike create(Long userId, Long postId) {
        validateUserId(userId);
        validatePostId(postId);

        return new PostLike(userId, postId);
    }

    private static void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(PostErrorCode.POST_USERID_NULL);
        }
    }

    private static void validatePostId(Long postId) {
        if (postId == null) {
            throw new BusinessException(PostErrorCode.POST_ID_NULL);
        }
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}
