package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "회원가입 요청 DTO")
public record SignupRequestDTO(

    @Schema(description = "사용자 아이디", example = "testuser123", minLength = 5, maxLength = 20)
    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자명은 5자 이상 20자 이하여야 합니다.")
    String userName,

    @Schema(
        description = "비밀번호 (8자 이상, 영문/숫자/특수문자 포함)",
        example = "Password123!"
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문/숫자/특수문자를 각각 포함해야 합니다."
    )
    String password,

    @Schema(description = "닉네임", example = "치과왕", maxLength = 10)
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 10자를 초과할 수 없습니다.")
    String nickName,

    @Schema(description = "생년월일", example = "1990-01-15")
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    LocalDate birthday,

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    @NotBlank(message = "주소는 필수입니다.")
    String address,

    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email,

    @Schema(description = "사용자 유형", example = "DENTIST")
    @NotNull(message = "사용자 유형은 필수입니다.")
    UserType userType
) {

}