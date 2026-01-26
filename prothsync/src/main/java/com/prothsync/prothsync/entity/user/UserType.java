package com.prothsync.prothsync.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    DENTIST("치과의사"),
    DENTAL_LAB_OWNER("기공소장"),
    DENTAL_TECHNICIAN("기공사"),
    STUDENT("학생");
    private final String description;
}
