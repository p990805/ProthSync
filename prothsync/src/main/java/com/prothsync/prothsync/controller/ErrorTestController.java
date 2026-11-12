package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.ErrorTestControllerDocs;
import com.prothsync.prothsync.dto.ErrorTestResponseDTO;
import com.prothsync.prothsync.dto.ErrorTestScenarioResponseDTO;
import com.prothsync.prothsync.vo.ErrorTestScenario;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

@Slf4j(topic = "ERROR_SIMULATION")
@RestController
@Profile("error-test & !prod & !production")
@RequestMapping("/api/test/errors")
public class ErrorTestController implements ErrorTestControllerDocs {

    @Override
    @GetMapping
    public List<ErrorTestScenarioResponseDTO> scenarios() {
        return Arrays.stream(ErrorTestScenario.values())
            .map(ErrorTestScenarioResponseDTO::from)
            .toList();
    }

    @Override
    @PostMapping("/{scenario}")
    public ResponseEntity<ErrorTestResponseDTO> trigger(@PathVariable ErrorTestScenario scenario) {
        String simulationId = UUID.randomUUID().toString();
        RuntimeException failure = null;

        try {
            simulate(scenario);
        } catch (RuntimeException exception) {
            // Capture the actual stack without propagating into the AOP/global error loggers.
            failure = exception;
        }

        String exceptionType = failure == null ? null : failure.getClass().getName();
        String message = "[ERROR_SIMULATION] " + scenario.name() + ": " + scenario.getDescription();
        log.atLevel(scenario.getLogLevel())
            .addKeyValue("event", "error_simulation")
            .addKeyValue("synthetic", true)
            .addKeyValue("simulation_id", simulationId)
            .addKeyValue("error_scenario", scenario.name())
            .addKeyValue("http_status", scenario.getHttpStatus().value())
            .addKeyValue("exception_type", exceptionType)
            .setCause(failure)
            .log(message);

        return ResponseEntity.status(scenario.getHttpStatus()).body(new ErrorTestResponseDTO(
            simulationId, scenario, scenario.getLogLevel().name(), scenario.getHttpStatus().value(),
            exceptionType, message));
    }

    private void simulate(ErrorTestScenario scenario) {
        switch (scenario) {
            case RUNTIME -> throw new RuntimeException("Synthetic runtime failure");
            case NULL_POINTER -> Objects.requireNonNull(null, "Synthetic missing dependency");
            case ARITHMETIC -> {
                int divisor = 0;
                int result = 1 / divisor;
            }
            case DATABASE_CONNECTION -> throw new DataAccessResourceFailureException(
                "Synthetic database connection failure",
                new SQLException("Synthetic connection refused", "08001"));
            case DATABASE_CONSTRAINT -> throw new DataIntegrityViolationException(
                "Synthetic duplicate key violation",
                new SQLException("Synthetic unique constraint violation", "23505"));
            case REDIS_CONNECTION -> throw new RedisConnectionFailureException(
                "Synthetic Redis connection failure", new ConnectException("Synthetic connection refused"));
            case EXTERNAL_TIMEOUT -> throw new ResourceAccessException(
                "Synthetic upstream API timeout", new SocketTimeoutException("Synthetic read timed out"));
            case NESTED_CAUSE -> throw new IllegalStateException("Synthetic workflow failure",
                new RuntimeException("Synthetic dependency failure", new IOException("Synthetic root cause")));
            case BAD_REQUEST -> throw new IllegalArgumentException("Synthetic invalid input");
            case WARN_ONLY, ERROR_LOG_ONLY -> {
                // Log-level tests deliberately have no exception or stack trace.
            }
        }
    }
}
