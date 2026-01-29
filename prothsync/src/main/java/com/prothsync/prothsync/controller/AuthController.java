package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.AuthControllerDocs;
import com.prothsync.prothsync.dto.LoginRequestDTO;
import com.prothsync.prothsync.dto.LoginResponseDTO;
import com.prothsync.prothsync.dto.SignupRequestDTO;
import com.prothsync.prothsync.dto.SignupResponseDTO;
import com.prothsync.prothsync.dto.TokenRefreshRequestDTO;
import com.prothsync.prothsync.dto.TokenRefreshResponseDTO;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
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

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        User user = authService.signup(signupRequest);
        return ResponseEntity.ok(SignupResponseDTO.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(
        @Valid @RequestBody TokenRefreshRequestDTO refreshRequest) {
        TokenRefreshResponseDTO response = authService.refreshToken(refreshRequest.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        HttpServletRequest request
    ) {
        String accessToken = resolveToken(request);
        authService.logout(userDetails.getUserId(), accessToken);
        return ResponseEntity.ok().build();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}