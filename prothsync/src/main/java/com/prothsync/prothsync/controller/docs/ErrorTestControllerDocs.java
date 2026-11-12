package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.ErrorTestResponseDTO;
import com.prothsync.prothsync.dto.ErrorTestScenarioResponseDTO;
import com.prothsync.prothsync.vo.ErrorTestScenario;
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

@Tag(name = "오류 시뮬레이션", description = "Loki/n8n 테스트용. error-test 프로필에서만 활성화됩니다.")
@SecurityRequirement(name = "bearerAuth")
public interface ErrorTestControllerDocs {

    @Operation(summary = "오류 시나리오 목록", description = "목록 조회 자체는 오류 로그를 생성하지 않습니다.")
    List<ErrorTestScenarioResponseDTO> scenarios();

    @Operation(summary = "모의 오류 한 건 발생",
        description = "선택한 예외/로그를 ERROR_SIMULATION 로거에 한 건 기록합니다. "
            + "실제 DB/Redis/외부 API는 호출하지 않습니다. HTTP 400/500/504는 의도된 테스트 응답입니다. "
            + "예외는 컨트롤러 내부에서 포착하므로 기존 전역 예외 처리기의 중복 ERROR 로그를 만들지 않습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "WARN_ONLY 또는 ERROR_LOG_ONLY",
            content = @Content(schema = @Schema(implementation = ErrorTestResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST (WARN), 또는 유효하지 않은 시나리오"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "500", description = "의도된 서버 오류 (ERROR)",
            content = @Content(schema = @Schema(implementation = ErrorTestResponseDTO.class))),
        @ApiResponse(responseCode = "504", description = "의도된 외부 API 타임아웃 (ERROR)",
            content = @Content(schema = @Schema(implementation = ErrorTestResponseDTO.class)))
    })
    ResponseEntity<ErrorTestResponseDTO> trigger(
        @Parameter(description = "발생시킬 시나리오", required = true) ErrorTestScenario scenario);
}
