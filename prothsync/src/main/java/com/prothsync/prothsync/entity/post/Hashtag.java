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
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseEntity {

    private static final int MAX_TAG_LENGTH = 50;
    private static final Pattern TAG_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9_]+$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @Column(nullable = false, unique = true, length = 50)
    private String tagName;

    private Hashtag(String tagName) {
        this.tagName = normalizeTagName(tagName);
    }

    public static Hashtag create(String tagName) {
        validateTagName(tagName);
        return new Hashtag(tagName);
    }

    private static void validateTagName(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            throw new BusinessException(PostErrorCode.POST_HASHTAG_NULL);
        }

        String normalized = normalizeTagName(tagName);

        if (normalized.length() > MAX_TAG_LENGTH) {
            throw new BusinessException(PostErrorCode.HASHTAG_TOO_LONG);
        }

        if (!TAG_PATTERN.matcher(normalized).matches()) {
            throw new BusinessException(PostErrorCode.HASHTAG_INVALID_PATTERN);
        }
    }

    private static String normalizeTagName(String tagName) {
        return tagName.trim()
            .replaceAll("^#+", "")
            .toLowerCase();
    }

    public String getDisplayName() {
        return "#" + this.tagName;
    }

}
