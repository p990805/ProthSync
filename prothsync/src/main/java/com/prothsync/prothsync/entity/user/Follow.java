package com.prothsync.prothsync.entity.user;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.FollowErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "follows",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_follows_follower_following",
            columnNames = {"follower_id", "following_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "following_id", nullable = false)
    private Long followingId;

    private Follow(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public static Follow create(Long followerId, Long followingId) {
        validateFollowerId(followerId);
        validateFollowingId(followingId);
        validateNotSelf(followerId, followingId);

        return new Follow(followerId, followingId);
    }

    private static void validateFollowerId(Long followerId) {
        if (followerId == null) {
            throw new BusinessException(FollowErrorCode.FOLLOWER_ID_NULL);
        }
    }

    private static void validateFollowingId(Long followingId) {
        if (followingId == null) {
            throw new BusinessException(FollowErrorCode.FOLLOWING_ID_NULL);
        }
    }

    private static void validateNotSelf(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(FollowErrorCode.CANNOT_FOLLOW_SELF);
        }
    }
}
