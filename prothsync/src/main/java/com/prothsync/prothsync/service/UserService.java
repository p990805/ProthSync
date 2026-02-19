package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.MyProfileResponseDTO;
import com.prothsync.prothsync.dto.NearbyUserResponseDTO;
import com.prothsync.prothsync.dto.ProfileUpdateRequestDTO;
import com.prothsync.prothsync.dto.UserProfileResponseDTO;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserType;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.repository.repository.FollowRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import com.prothsync.prothsync.service.GeocodingService.GeocodingResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final double DEFAULT_RADIUS_KM = 5.0;
    private static final double MAX_RADIUS_KM = 50.0;
    private static final double MIN_RADIUS_KM = 1.0;
    private static final int DEFAULT_LIMIT = 20;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final GeocodingService geocodingService;

    // ============================================================
    // 프로필 조회/수정
    // ============================================================

    /**
     * 내 프로필 조회 (전체 정보 포함)
     */
    @Transactional(readOnly = true)
    public MyProfileResponseDTO getMyProfile(Long userId) {
        User user = findUserOrThrow(userId);
        return MyProfileResponseDTO.from(user);
    }

    /**
     * 다른 사용자 프로필 조회 (공개 정보 + 팔로우 여부)
     */
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserProfile(Long targetUserId, Long currentUserId) {
        User targetUser = findUserOrThrow(targetUserId);

        boolean isFollowing = false;
        if (currentUserId != null && !currentUserId.equals(targetUserId)) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(
                currentUserId, targetUserId);
        }

        return UserProfileResponseDTO.of(targetUser, isFollowing);
    }

    /**
     * 프로필 수정 (부분 수정 지원)
     */
    @Transactional
    public MyProfileResponseDTO updateProfile(Long userId, ProfileUpdateRequestDTO request) {
        User user = findUserOrThrow(userId);

        if (request.nickName() != null) {
            validateNickNameNotDuplicate(userId, request.nickName());
            user.updateNickName(request.nickName());
        }
        if (request.bio() != null) {
            user.updateBio(request.bio());
        }
        if (request.profileImageUrl() != null) {
            user.updateProfileImageUrl(request.profileImageUrl());
        }
        if (request.email() != null) {
            validateEmailNotDuplicate(userId, request.email());
            user.updateEmail(request.email());
        }
        if (request.address() != null) {
            user.updateAddress(request.address());
            geocodeAndUpdateCoordinates(user, request.address());
        }

        userRepository.save(user);
        return MyProfileResponseDTO.from(user);
    }

    // ============================================================
    // 주변 사용자 검색
    // ============================================================

    /**
     * 현재 사용자 기준 주변 사업체(치과, 기공소) 검색
     */
    @Transactional(readOnly = true)
    public List<NearbyUserResponseDTO> findNearbyUsers(
        Long currentUserId, Double radiusKm, Integer limit) {

        User currentUser = findUserOrThrow(currentUserId);
        validateUserHasCoordinates(currentUser);

        double radius = resolveRadius(radiusKm);
        int resultLimit = resolveLimit(limit);

        List<String> searchableTypes = UserType.getSearchableTypeNames();

        List<User> nearbyUsers = userRepository.findNearbyUsers(
            currentUser.getLatitude(),
            currentUser.getLongitude(),
            radius,
            currentUserId,
            searchableTypes,
            resultLimit
        );

        return nearbyUsers.stream()
            .map(user -> NearbyUserResponseDTO.of(user,
                calculateDistance(currentUser, user)))
            .toList();
    }

    /**
     * 현재 사용자 기준 특정 유형의 주변 사업체 검색
     * (치과 또는 기공소만 검색 가능)
     */
    @Transactional(readOnly = true)
    public List<NearbyUserResponseDTO> findNearbyUsersByType(
        Long currentUserId, UserType userType, Double radiusKm, Integer limit) {

        User currentUser = findUserOrThrow(currentUserId);
        validateUserHasCoordinates(currentUser);
        validateSearchableType(userType);

        double radius = resolveRadius(radiusKm);
        int resultLimit = resolveLimit(limit);

        List<User> nearbyUsers = userRepository.findNearbyUsersByType(
            currentUser.getLatitude(),
            currentUser.getLongitude(),
            radius,
            currentUserId,
            userType.name(),
            resultLimit
        );

        return nearbyUsers.stream()
            .map(user -> NearbyUserResponseDTO.of(user,
                calculateDistance(currentUser, user)))
            .toList();
    }

    /**
     * 사용자 주소 변경 시 좌표 재계산
     */
    @Transactional
    public void updateUserAddress(Long userId, String newAddress) {
        User user = findUserOrThrow(userId);
        user.updateAddress(newAddress);
        geocodeAndUpdateCoordinates(user, newAddress);
        userRepository.save(user);
    }

    /**
     * 좌표가 없는 사용자의 좌표를 수동으로 재시도
     */
    @Transactional
    public void retryGeocoding(Long userId) {
        User user = findUserOrThrow(userId);

        GeocodingResult result = geocodingService.geocode(user.getAddress());
        if (result.success()) {
            user.updateCoordinates(result.latitude(), result.longitude());
            userRepository.save(user);
            log.info("지오코딩 재시도 성공 - userId: {}", userId);
        } else {
            throw new BusinessException(UserErrorCode.GEOCODING_FAILED);
        }
    }

    // ============================================================
    // Private helpers
    // ============================================================

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateUserHasCoordinates(User user) {
        if (!user.hasCoordinates()) {
            throw new BusinessException(UserErrorCode.USER_COORDINATES_NOT_SET);
        }
    }

    /**
     * 위치 검색 가능한 사용자 유형인지 검증
     */
    private void validateSearchableType(UserType userType) {
        if (!userType.isSearchableByLocation()) {
            throw new BusinessException(UserErrorCode.UNSEARCHABLE_USER_TYPE);
        }
    }

    /**
     * 닉네임 중복 체크 (본인 제외)
     */
    private void validateNickNameNotDuplicate(Long userId, String nickName) {
        userRepository.findByNickName(nickName)
            .filter(existing -> !existing.getUserId().equals(userId))
            .ifPresent(existing -> {
                throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
            });
    }

    /**
     * 이메일 중복 체크 (본인 제외)
     */
    private void validateEmailNotDuplicate(Long userId, String email) {
        userRepository.findByEmail(email)
            .filter(existing -> !existing.getUserId().equals(userId))
            .ifPresent(existing -> {
                throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
            });
    }

    /**
     * 주소 → 좌표 변환 후 사용자 좌표 업데이트
     */
    private void geocodeAndUpdateCoordinates(User user, String address) {
        GeocodingResult result = geocodingService.geocode(address);
        if (result.success()) {
            user.updateCoordinates(result.latitude(), result.longitude());
            log.info("주소 변경 지오코딩 성공 - userId: {}, 주소: {}", user.getUserId(), address);
        } else {
            user.updateCoordinates(null, null);
            log.warn("주소 변경 지오코딩 실패 - userId: {}, 주소: {}", user.getUserId(), address);
        }
    }

    private double resolveRadius(Double radiusKm) {
        if (radiusKm == null) {
            return DEFAULT_RADIUS_KM;
        }
        if (radiusKm < MIN_RADIUS_KM || radiusKm > MAX_RADIUS_KM) {
            throw new BusinessException(UserErrorCode.INVALID_SEARCH_RADIUS);
        }
        return radiusKm;
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, 50);
    }

    /**
     * Haversine 공식으로 두 사용자 간 거리(km) 계산
     */
    private double calculateDistance(User from, User to) {
        double earthRadiusKm = 6371.0;

        double latDistance = Math.toRadians(to.getLatitude() - from.getLatitude());
        double lngDistance = Math.toRadians(to.getLongitude() - from.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(from.getLatitude()))
            * Math.cos(Math.toRadians(to.getLatitude()))
            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }
}