package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.vo.ErrorTestScenario;

public record ErrorTestScenarioResponseDTO(
    ErrorTestScenario scenario,
    String description,
    String logLevel,
    int httpStatus
) {
    public static ErrorTestScenarioResponseDTO from(ErrorTestScenario scenario) {
        return new ErrorTestScenarioResponseDTO(scenario, scenario.getDescription(),
            scenario.getLogLevel().name(), scenario.getHttpStatus().value());
    }
}
