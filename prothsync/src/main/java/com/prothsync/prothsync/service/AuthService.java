package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.LoginRequestDTO;
import com.prothsync.prothsync.dto.SignupRequestDTO;
import com.prothsync.prothsync.entity.RefreshToken;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.AuthErrorCode;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.repository.repository.RefreshTokenRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import com.prothsync.prothsync.security.JwtTokenProvider;
import com.prothsync.prothsync.service.GeocodingService.GeocodingResult;
import com.prothsync.prothsync.vo.LoginResult;
import com.prothsync.prothsync.vo.RefreshResult;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final GeocodingService geocodingService;

    @Transactional
    public User signup(SignupRequestDTO signupRequest) {
        validateDuplicateUserName(signupRequest.userName());
        validateDuplicateNickName(signupRequest.nickName());
        validateDuplicateEmail(signupRequest.email());

        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        User user = User.createUser(
            signupRequest.userName(),
            encodedPassword,
            signupRequest.nickName(),
            signupRequest.birthday(),
            signupRequest.address(),
            signupRequest.email(),
            signupRequest.userType()
        );
        geocodeAndSetCoordinates(user, signupRequest.address());

        return userRepository.save(user);
    }

    @Transactional
    public LoginResult login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUserName(loginRequest.userName())
            .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (user.isSuspended()) {
            throw new BusinessException(AuthErrorCode.USER_SUSPENDED);
        }
        if (user.hasExpiredSuspension()) {
            user.unsuspend();
            userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getUserName(), user.getUserType());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        saveOrUpdateRefreshToken(user.getUserId(), refreshToken);

        return new LoginResult(
            accessToken,
            refreshToken,
            user.getUserId(),
            user.getUserName(),
            user.getNickName()
        );
    }

    @Transactional
    public RefreshResult refreshToken(String refreshTokenValue) {
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtTokenProvider.isRefreshToken(refreshTokenValue)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (storedToken.isExpired()) {
            refreshTokenRepository.deleteByUserId(storedToken.getUserId());
            throw new BusinessException(AuthErrorCode.EXPIRED_TOKEN);
        }

        Long userId = storedToken.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.isSuspended()) {
            throw new BusinessException(AuthErrorCode.USER_SUSPENDED);
        }
        if (user.hasExpiredSuspension()) {
            user.unsuspend();
            userRepository.save(user);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getUserName(), user.getUserType());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        storedToken.updateToken(newRefreshToken, jwtTokenProvider.getRefreshTokenExpiration());
        refreshTokenRepository.save(storedToken);

        return new RefreshResult(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId, String accessToken) {
        refreshTokenRepository.deleteByUserId(userId);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Duration remainingTime = jwtTokenProvider.getRemainingTime(accessToken);
            tokenBlacklistService.addToBlackList(accessToken, remainingTime);
        }
    }

    private void geocodeAndSetCoordinates(User user, String address) {
        GeocodingResult result = geocodingService.geocode(address);

        if (result.success()) {
            user.updateCoordinates(result.latitude(), result.longitude());
            log.info("회원가입 지오코딩 성공 - 주소: {}, 위도: {}, 경도: {}",
                address, result.latitude(), result.longitude());
        } else {
            log.warn("회원가입 지오코딩 실패 - 주소: {}. 좌표 없이 저장합니다.", address);
        }
    }

    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        refreshTokenRepository.findByUserId(userId)
            .ifPresentOrElse(
                token -> {
                    token.updateToken(refreshToken, jwtTokenProvider.getRefreshTokenExpiration());
                    refreshTokenRepository.save(token);
                },
                () -> {
                    RefreshToken newToken = RefreshToken.create(
                        userId,
                        refreshToken,
                        jwtTokenProvider.getRefreshTokenExpiration()
                    );
                    refreshTokenRepository.save(newToken);
                }
            );
    }

    private void validateDuplicateUserName(String userName) {
        if (userRepository.existsByUserName(userName)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateNickName(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }
}