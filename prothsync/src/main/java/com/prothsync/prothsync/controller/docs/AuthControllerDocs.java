package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.LoginRequestDTO;
import com.prothsync.prothsync.dto.LoginResponseDTO;
import com.prothsync.prothsync.dto.SignupRequestDTO;
import com.prothsync.prothsync.dto.SignupResponseDTO;
import com.prothsync.prothsync.dto.TokenRefreshResponseDTO;
import com.prothsync.prothsync.exception.ErrorResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃, 토큰 갱신 API")
public interface AuthControllerDocs {

    @Operation(summary = "회원가입", description = "신규 사용자를 등록합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = SignupResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패 (필수 필드 누락, 형식 오류 등)",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "중복된 아이디/닉네임/이메일",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<SignupResponseDTO> signup(SignupRequestDTO signupRequest);

    @Operation(
        summary = "로그인",
        description = "아이디와 비밀번호로 로그인합니다. "
            + "Access Token은 응답 body로, Refresh Token은 HttpOnly Cookie로 발급됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공 (Set-Cookie 헤더에 refreshToken 포함)",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "아이디 또는 비밀번호 불일치",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "423",
            description = "계정 잠금 (로그인 시도 초과)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<LoginResponseDTO> login(LoginRequestDTO loginRequest, HttpServletResponse response);

    @Operation(
        summary = "토큰 갱신",
        description = "HttpOnly Cookie의 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다. "
            + "새로운 Refresh Token은 HttpOnly Cookie로 갱신됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "토큰 갱신 성공 (Set-Cookie 헤더에 새 refreshToken 포함)",
            content = @Content(schema = @Schema(implementation = TokenRefreshResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Refresh Token 쿠키 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않거나 만료된 Refresh Token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<TokenRefreshResponseDTO> refreshToken(
        @Parameter(description = "HttpOnly Cookie에서 자동 전송되는 Refresh Token", hidden = true)
        String refreshToken,
        HttpServletResponse response
    );

    @Operation(
        summary = "로그아웃",
        description = "현재 사용자를 로그아웃하고 토큰을 무효화합니다. Refresh Token 쿠키도 삭제됩니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요 또는 유효하지 않은 토큰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> logout(CustomUserDetails userDetails, HttpServletRequest request, HttpServletResponse response);
}