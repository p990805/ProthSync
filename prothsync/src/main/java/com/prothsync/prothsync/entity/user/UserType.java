package com.prothsync.prothsync.entity.user;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    DENTAL_CLINIC("치과"),
    DENTAL_LAB("기공소"),
    DENTAL_TECHNICIAN("기공사"),
    STUDENT("학생");

    private final String description;

    /**
     * 위치 기반 검색 대상인지 여부
     * 사업체(치과, 기공소)만 검색 가능
     */
    public boolean isSearchableByLocation() {
        return this == DENTAL_CLINIC || this == DENTAL_LAB;
    }

    /**
     * 위치 검색 가능한 UserType 목록 반환
     */
    public static List<String> getSearchableTypeNames() {
        return Arrays.stream(values())
            .filter(UserType::isSearchableByLocation)
            .map(Enum::name)
            .toList();
    }
}