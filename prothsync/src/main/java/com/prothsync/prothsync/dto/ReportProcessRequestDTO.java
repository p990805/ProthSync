package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.report.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record ReportProcessRequestDTO(

    @NotNull(message = "처리 상태는 필수입니다.")
    ReportStatus status
) {

}