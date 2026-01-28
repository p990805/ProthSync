package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record SignupRequestDTO(

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자명은 5자 이상 20자 이하여야 합니다.")
    String userName,

    @NotBlank(message = "비밀번호는 필수입니다.")
    String password,

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 10자를 초과할 수 없습니다.")
    String nickName,

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    LocalDate birthday,

    @NotBlank(message = "주소는 필수입니다.")
    String address,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email,

    @NotNull(message = "사용자 유형은 필수입니다.")
    UserType userType
) {

}
