package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.vo.ErrorTestScenario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "모의 오류 실행 결과. HTTP 4xx/5xx도 의도된 테스트 응답입니다.")
public record ErrorTestResponseDTO(
    @Schema(description = "Loki 조회 및 n8n 중복 제거에 사용할 고유 ID") String simulationId,
    ErrorTestScenario scenario,
    String logLevel,
    int httpStatus,
    @Schema(description = "발생한 예외 클래스. 로그만 기록한 경우 null") String exceptionType,
    String message
) {
}
