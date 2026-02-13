package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.NearbyUserResponseDTO;
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

@Tag(name = "사용자", description = "사용자 정보 및 주변 검색 API")
@SecurityRequirement(name = "bearerAuth")
public interface UserControllerDocs {

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