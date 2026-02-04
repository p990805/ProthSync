package com.prothsync.prothsync.entity.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    WORK_SHOWCASE("작업물"),
    EQUIPMENT("장비/재료"),
    TECHNIQUE("기술/노하우"),
    DAILY("일상"),
    QUESTION("질문"),
    INFO("정보공유");

    private final String description;
}
