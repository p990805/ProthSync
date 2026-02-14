package com.prothsync.prothsync.entity.caserequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaseCategory {
    CROWN("크라운"),
    BRIDGE("브릿지"),
    DENTURE("의치"),
    IMPLANT("임플란트"),
    INLAY_ONLAY("인레이/온레이"),
    VENEER("비니어"),
    ORTHODONTIC("교정장치"),
    OTHER("기타");

    private final String description;
}