package com.prothsync.prothsync.entity.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {

    SPAM("스팸/광고"),
    INAPPROPRIATE("부적절한 콘텐츠"),
    HARASSMENT("괴롭힘/따돌림"),
    COPYRIGHT("저작권 침해"),
    OTHER("기타");

    private final String description;
}
