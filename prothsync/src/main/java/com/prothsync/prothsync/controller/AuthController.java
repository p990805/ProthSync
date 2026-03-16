package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.AuthControllerDocs;
import com.prothsync.prothsync.dto.LoginRequestDTO;
import com.prothsync.prothsync.dto.LoginResponseDTO;
import com.prothsync.prothsync.dto.SignupRequestDTO;
import com.prothsync.prothsync.dto.SignupResponseDTO;
import com.prothsync.prothsync.dto.TokenRefreshResponseDTO;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.AuthService;
import com.prothsync.prothsync.vo.LoginResult;
import com.prothsync.prothsync.vo.RefreshResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일 (초)

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        User user = authService.signup(signupRequest);
        return ResponseEntity.ok(SignupResponseDTO.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
        @Valid @RequestBody LoginRequestDTO loginRequest,
        HttpServletResponse response
    ) {
        LoginResult result = authService.login(loginRequest);

        addRefreshTokenCookie(response, result.refreshToken());

        return ResponseEntity.ok(LoginResponseDTO.of(
            result.accessToken(),
            result.userId(),
            result.userName(),
            result.nickName()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(
        @CookieValue(name = REFRESH_TOKEN_COOKIE) String refreshToken,
        HttpServletResponse response
    ) {
        RefreshResult result = authService.refreshToken(refreshToken);

        addRefreshTokenCookie(response, result.refreshToken());

        return ResponseEntity.ok(TokenRefreshResponseDTO.of(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String accessToken = resolveToken(request);
        authService.logout(userDetails.getUserId(), accessToken);

        clearRefreshTokenCookie(response);

        return ResponseEntity.ok().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(REFRESH_TOKEN_MAX_AGE)
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(0)
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}