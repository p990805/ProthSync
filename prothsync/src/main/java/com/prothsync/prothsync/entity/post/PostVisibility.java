package com.prothsync.prothsync.entity.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostVisibility {

    PUBLIC("전체 공개"),
    FOLLOWERS_ONLY("팔로워 공개"),
    PRIVATE("비공개");

    private final String description;
}
