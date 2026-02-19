package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.MyProfileResponseDTO;
import com.prothsync.prothsync.dto.NearbyUserResponseDTO;
import com.prothsync.prothsync.dto.ProfileUpdateRequestDTO;
import com.prothsync.prothsync.dto.UserProfileResponseDTO;
import com.prothsync.prothsync.entity.user.UserType;
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
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자", description = "사용자 프로필 및 주변 검색 API")
@SecurityRequirement(name = "bearerAuth")
public interface UserControllerDocs {

    @Operation(
        summary = "내 프로필 조회",
        description = "현재 로그인한 사용자의 전체 프로필 정보를 조회합니다. (이메일, 생년월일, 좌표 등 민감 정보 포함)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<MyProfileResponseDTO> getMyProfile(CustomUserDetails userDetails);

    @Operation(
        summary = "사용자 프로필 조회",
        description = "특정 사용자의 공개 프로필 정보를 조회합니다. 로그인 사용자 기준 팔로우 여부를 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<UserProfileResponseDTO> getUserProfile(
        @Parameter(description = "조회할 사용자 ID") Long userId,
        CustomUserDetails userDetails
    );

    @Operation(
        summary = "프로필 수정",
        description = "현재 로그인한 사용자의 프로필을 수정합니다. 변경할 필드만 전달하면 됩니다 (부분 수정 지원). "
            + "주소 변경 시 좌표가 자동으로 재계산됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "유효하지 않은 입력값",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "닉네임 또는 이메일 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<MyProfileResponseDTO> updateProfile(
        CustomUserDetails userDetails,
        ProfileUpdateRequestDTO request
    );


    @Operation(
        summary = "주변 사업체 검색",
        description = "현재 로그인한 사용자 위치 기준으로 반경 내 사업체(치과, 기공소)를 검색합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "위치 정보 미설정 또는 잘못된 파라미터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<NearbyUserResponseDTO>> getNearbyUsers(
        CustomUserDetails userDetails,
        @Parameter(description = "검색 반경 (km, 기본값: 5, 최대: 50)") Double radiusKm,
        @Parameter(description = "최대 결과 수 (기본값: 20)") Integer limit
    );

    @Operation(
        summary = "유형별 주변 사업체 검색",
        description = "현재 위치 기준으로 특정 유형의 주변 사업체를 검색합니다. 치과(DENTAL_CLINIC) 또는 기공소(DENTAL_LAB)만 검색 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "위치 정보 미설정, 잘못된 파라미터 또는 검색 불가능한 사용자 유형",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<NearbyUserResponseDTO>> getNearbyUsersByType(
        CustomUserDetails userDetails,
        @Parameter(description = "사업체 유형 (DENTAL_CLINIC: 치과, DENTAL_LAB: 기공소)")
        UserType userType,
        @Parameter(description = "검색 반경 (km)") Double radiusKm,
        @Parameter(description = "최대 결과 수") Integer limit
    );

    @Operation(
        summary = "위치 정보 재설정",
        description = "현재 사용자의 주소로 좌표를 다시 계산합니다. 회원가입 시 지오코딩 실패한 경우 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좌표 재설정 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "좌표 변환 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> retryGeocoding(CustomUserDetails userDetails);
}