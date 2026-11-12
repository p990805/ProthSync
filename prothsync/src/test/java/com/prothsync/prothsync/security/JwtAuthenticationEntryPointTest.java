package com.prothsync.prothsync.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationEntryPointTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint(mapper);
    private final Logger logger = (Logger) LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private ListAppender<ILoggingEvent> events;
    private Level previousLevel;
    private boolean previousAdditive;

    @BeforeEach
    void captureLogs() {
        previousLevel = logger.getLevel();
        previousAdditive = logger.isAdditive();
        logger.setLevel(Level.WARN);
        logger.setAdditive(false);
        events = new ListAppender<>();
        events.start();
        logger.addAppender(events);
    }

    @AfterEach
    void restoreLogger() {
        logger.detachAppender(events);
        events.stop();
        logger.setLevel(previousLevel);
        logger.setAdditive(previousAdditive);
    }

    @Test
    void recordsRequestSourceAndPreservesUnauthorizedResponse() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/notifications");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("User-Agent", "MonitoringClient/1.0");
        var response = commence(request);

        assertThat(response.getStatus()).isEqualTo(401);
        var body = mapper.readTree(response.getContentAsString());
        assertThat(body.path("errorName").asText()).isEqualTo("UNAUTHORIZED");
        assertThat(body.path("message").asText()).isEqualTo("인증이 필요합니다.");
        assertThat(events.list).hasSize(1);
        assertThat(events.list.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(fields()).containsEntry("event", "authentication_required")
            .containsEntry("http_status", "401")
            .containsEntry("http_method", "GET")
            .containsEntry("request_path", "/api/notifications")
            .containsEntry("client_ip", "127.0.0.1")
            .containsEntry("user_agent", "MonitoringClient/1.0")
            .containsEntry("authorization_present", "false")
            .containsEntry("bearer_token_present", "false");
        assertThat(events.list.get(0).getFormattedMessage())
            .contains("method=GET", "path=/api/notifications", "ip=127.0.0.1", "MonitoringClient/1.0");
    }

    @Test
    void neverRecordsCredentialsQueryBodyOrExceptionMessage() throws Exception {
        var request = new MockHttpServletRequest("POST", "/api/test/errors/RUNTIME");
        request.addHeader("Authorization", "Bearer private-access-token");
        request.addHeader("Cookie", "refreshToken=private-refresh-token");
        request.addHeader("Referer", "http://localhost/?token=private-referer-token");
        request.setQueryString("token=private-query-token");
        request.setContent("private-body-password".getBytes(StandardCharsets.UTF_8));
        entryPoint.commence(request, new MockHttpServletResponse(),
            new InsufficientAuthenticationException("private-exception-token"));

        assertThat(fields()).containsEntry("authorization_present", "true")
            .containsEntry("bearer_token_present", "true");
        String recorded = fields() + events.list.get(0).getFormattedMessage();
        assertThat(recorded).doesNotContain("private-access-token", "private-refresh-token",
            "private-query-token", "private-body-password", "private-referer-token", "private-exception-token");
        assertThat(events.list.get(0).getThrowableProxy()).isNull();
    }

    @Test
    void recordsOriginalPathForErrorDispatch() throws Exception {
        var request = new MockHttpServletRequest("GET", "/error");
        request.setDispatcherType(DispatcherType.ERROR);
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/actuator/info");
        commence(request);

        assertThat(fields()).containsEntry("request_path", "/error")
            .containsEntry("original_request_path", "/actuator/info")
            .containsEntry("dispatcher_type", "ERROR");
    }

    @Test
    void sanitizesAndBoundsUserAgent() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/notifications");
        request.addHeader("User-Agent", "client\r\nforged\t" + "x".repeat(600));
        commence(request);

        assertThat(fields().get("user_agent")).startsWith("client  forged ")
            .endsWith("...").hasSize(515).doesNotContain("\r", "\n", "\t");
        assertThat(events.list.get(0).getFormattedMessage()).doesNotContain("\r", "\n", "\t");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Basic credentials", "Bearer ", "Bearer"})
    void distinguishesAuthorizationHeaderFromBearerToken(String authorization) throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/notifications");
        request.addHeader("Authorization", authorization);
        commence(request);

        assertThat(fields()).containsEntry("authorization_present", "true")
            .containsEntry("bearer_token_present", "false")
            .containsEntry("user_agent", "-");
    }

    private MockHttpServletResponse commence(MockHttpServletRequest request) throws Exception {
        var response = new MockHttpServletResponse();
        entryPoint.commence(request, response, new InsufficientAuthenticationException("Authentication required"));
        return response;
    }

    private Map<String, String> fields() {
        return events.list.get(0).getKeyValuePairs().stream()
            .collect(Collectors.toMap(pair -> pair.key, pair -> String.valueOf(pair.value)));
    }
}
