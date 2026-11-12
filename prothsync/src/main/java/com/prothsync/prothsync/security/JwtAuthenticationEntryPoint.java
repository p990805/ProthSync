package com.prothsync.prothsync.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prothsync.prothsync.exception.AuthErrorCode;
import com.prothsync.prothsync.exception.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        String method = logValue(request.getMethod());
        String path = logValue(request.getRequestURI());
        String clientIp = logValue(request.getRemoteAddr());
        String userAgent = logValue(request.getHeader("User-Agent"));
        String authorization = request.getHeader("Authorization");
        boolean authorizationPresent = StringUtils.hasText(authorization);
        boolean bearerTokenPresent = authorizationPresent && authorization.startsWith("Bearer ")
            && StringUtils.hasText(authorization.substring(7));
        String dispatcherType = request.getDispatcherType().name();
        Object originalUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String originalPath = originalUri instanceof String uri ? logValue(uri) : path;

        // Record request identity, never credentials, query strings or request bodies.
        log.atWarn()
            .addKeyValue("event", "authentication_required")
            .addKeyValue("http_status", HttpServletResponse.SC_UNAUTHORIZED)
            .addKeyValue("http_method", method)
            .addKeyValue("request_path", path)
            .addKeyValue("client_ip", clientIp)
            .addKeyValue("user_agent", userAgent)
            .addKeyValue("authorization_present", authorizationPresent)
            .addKeyValue("bearer_token_present", bearerTokenPresent)
            .addKeyValue("dispatcher_type", dispatcherType)
            .addKeyValue("original_request_path", originalPath)
            .addKeyValue("auth_exception", authException.getClass().getSimpleName())
            .log("Unauthorized request: method={} path={} ip={} user_agent={} "
                    + "authorization_present={} bearer_token_present={} dispatcher_type={} original_path={}",
                method, path, clientIp, userAgent, authorizationPresent, bearerTokenPresent,
                dispatcherType, originalPath);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(AuthErrorCode.UNAUTHORIZED);

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private String logValue(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        String sanitized = value.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ');
        return sanitized.length() > 512 ? sanitized.substring(0, 512) + "..." : sanitized;
    }
}
