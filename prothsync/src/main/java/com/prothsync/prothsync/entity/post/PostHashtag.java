package com.prothsync.prothsync.entity.post;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
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
    name = "post_hashtags",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_post_hashtags_post_hashtag",
            columnNames = {"post_id", "hashtag_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHashtag  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postHashtagId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "hashtag_id", nullable = false)
    private Long hashtagId;

    private PostHashtag(Long postId, Long hashtagId) {
        this.postId = postId;
        this.hashtagId = hashtagId;
    }
    public static PostHashtag create(Long postId, Long hashtagId) {
        validatePostId(postId);
        validateHashtagId(hashtagId);

        return new PostHashtag(postId, hashtagId);
    }

    private static void validatePostId(Long postId) {
        if (postId == null) {
            throw new BusinessException(PostErrorCode.POST_ID_NULL);
        }
    }

    private static void validateHashtagId(Long hashtagId) {
        if (hashtagId == null) {
            throw new BusinessException(PostErrorCode.POST_HASHTAG_ID_NULL);
        }
    }

}
