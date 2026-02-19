package com.prothsync.prothsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "프로필 수정 요청 DTO")
public record ProfileUpdateRequestDTO(

    @Schema(description = "닉네임", example = "새닉네임")
    @Size(max = 10, message = "닉네임은 10자를 초과할 수 없습니다.")
    String nickName,

    @Schema(description = "자기소개", example = "안녕하세요, 보철 전문 치과의사입니다.")
    @Size(max = 200, message = "자기소개는 200자를 초과할 수 없습니다.")
    String bio,

    @Schema(description = "프로필 이미지 URL", example = "https://s3.amazonaws.com/prothsync/profiles/1.jpg")
    String profileImageUrl,

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 456")
    String address,

    @Schema(description = "이메일", example = "new@example.com")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email
) {
}