package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequestDTO(

    @NotNull(message = "변경할 권한은 필수입니다.")
    UserRole role
) {

}