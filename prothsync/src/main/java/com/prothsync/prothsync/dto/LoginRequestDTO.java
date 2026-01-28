package com.prothsync.prothsync.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

    @NotBlank(message = "아이디는 필수입니다.")
    String userName,

    @NotBlank(message = "비밀번호는 필수입니다.")
    String password
) {
}
