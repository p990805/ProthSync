package com.prothsync.prothsync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UserSuspendRequestDTO(

    @NotBlank(message = "정지 사유는 필수입니다.")
    @Size(max = 500, message = "정지 사유는 500자를 초과할 수 없습니다.")
    String reason,

    LocalDateTime suspendUntil
) {

}