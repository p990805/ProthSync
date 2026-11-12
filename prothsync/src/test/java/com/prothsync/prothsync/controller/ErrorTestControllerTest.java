package com.prothsync.prothsync.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prothsync.prothsync.config.SecurityConfig;
import com.prothsync.prothsync.security.CustomUserDetailsService;
import com.prothsync.prothsync.security.JwtAuthenticationEntryPoint;
import com.prothsync.prothsync.security.JwtAuthenticationFilter;
import com.prothsync.prothsync.security.JwtTokenProvider;
import com.prothsync.prothsync.service.TokenBlacklistService;
import com.prothsync.prothsync.vo.ErrorTestScenario;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorTestController.class, properties = {
    "logging.file.name=build/test-logs/error-test.json",
    "logging.logback.rollingpolicy.file-name-pattern=build/test-logs/archive/error-test.%d{yyyy-MM-dd}.%i.json.gz"
})
@ActiveProfiles("error-test")
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
class ErrorTestControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    TokenBlacklistService tokenBlacklistService;

    private final Logger logger = (Logger) LoggerFactory.getLogger("ERROR_SIMULATION");
    private ListAppender<ILoggingEvent> events;

    @BeforeEach
    void captureLogs() {
        events = new ListAppender<>();
        events.start();
        logger.addAppender(events);
    }

    @AfterEach
    void detachAppender() {
        logger.detachAppender(events);
        events.stop();
    }

    @ParameterizedTest
    @EnumSource(ErrorTestScenario.class)
    void emitsOneCorrelatedEventWithExpectedStatusAndStack(ErrorTestScenario scenario) throws Exception {
        var result = mvc.perform(post("/api/test/errors/{scenario}", scenario).with(user("tester")))
            .andExpect(status().is(scenario.getHttpStatus().value()))
            .andExpect(jsonPath("$.scenario").value(scenario.name()))
            .andExpect(jsonPath("$.logLevel").value(scenario.getLogLevel().name()))
            .andReturn();

        var response = mapper.readTree(result.getResponse().getContentAsString());
        String id = response.path("simulationId").asText();
        assertThat(UUID.fromString(id).toString()).isEqualTo(id);
        assertThat(events.list).hasSize(1);
        var event = events.list.get(0);
        assertThat(event.getLevel().toString()).isEqualTo(scenario.getLogLevel().name());
        var fields = event.getKeyValuePairs().stream()
            .collect(Collectors.toMap(pair -> pair.key, pair -> String.valueOf(pair.value)));
        assertThat(fields).containsEntry("simulation_id", id)
            .containsEntry("error_scenario", scenario.name())
            .containsEntry("synthetic", "true")
            .containsEntry("http_status", Integer.toString(scenario.getHttpStatus().value()));

        if (scenario == ErrorTestScenario.WARN_ONLY || scenario == ErrorTestScenario.ERROR_LOG_ONLY) {
            assertThat(event.getThrowableProxy()).isNull();
            assertThat(response.path("exceptionType").isNull()).isTrue();
        } else {
            assertThat(event.getThrowableProxy()).isNotNull();
            assertThat(event.getThrowableProxy().getClassName())
                .isEqualTo(response.path("exceptionType").asText());
            if (scenario == ErrorTestScenario.NESTED_CAUSE) {
                assertThat(event.getThrowableProxy().getCause().getCause().getClassName())
                    .isEqualTo("java.io.IOException");
            }
        }
    }

    @Test
    void listsAllScenariosWithoutGeneratingErrors() throws Exception {
        mvc.perform(get("/api/test/errors").with(user("tester")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(ErrorTestScenario.values().length));
        assertThat(events.list).isEmpty();
    }

    @Test
    void requiresAuthentication() throws Exception {
        mvc.perform(post("/api/test/errors/RUNTIME"))
            .andExpect(status().isUnauthorized());
        assertThat(events.list).isEmpty();
    }

    @Test
    void rejectsUnknownScenarioWithoutGeneratingSimulation() throws Exception {
        mvc.perform(post("/api/test/errors/UNKNOWN").with(user("tester")))
            .andExpect(status().isBadRequest());
        assertThat(events.list).isEmpty();
    }

    @Test
    void eachCallHasADifferentSimulationId() throws Exception {
        String first = simulateRuntime();
        String second = simulateRuntime();
        assertThat(first).isNotEqualTo(second);
    }

    private String simulateRuntime() throws Exception {
        var result = mvc.perform(post("/api/test/errors/RUNTIME").with(user("tester")))
            .andExpect(status().isInternalServerError()).andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).path("simulationId").asText();
    }
}
