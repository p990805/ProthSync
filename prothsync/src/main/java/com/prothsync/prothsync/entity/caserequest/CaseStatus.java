package com.prothsync.prothsync.entity.caserequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaseStatus {
    OPEN("모집중"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String description;

    public boolean isOpen() {
        return this == OPEN;
    }

    public boolean isTerminated() {
        return this == COMPLETED || this == CANCELLED;
    }
}