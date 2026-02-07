package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.Hashtag;

public record HashtagResponseDTO(
    Long hashtagId,
    String tagName,
    String displayName,
    int usageCount
) {

    public static HashtagResponseDTO from(Hashtag hashtag) {
        return new HashtagResponseDTO(
            hashtag.getHashtagId(),
            hashtag.getTagName(),
            hashtag.getDisplayName(),
            hashtag.getUsageCount()
        );
    }
}
