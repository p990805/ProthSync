package com.prothsync.prothsync.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDTO(

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    String refreshToken
) {
}
