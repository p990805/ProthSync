package com.prothsync.prothsync.config;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class StructuredLoggingTest {

    @TempDir
    Path directory;

    @Test
    void writesQueryableJsonWithCompleteExceptionOnOneLine() throws Exception {
        Path file = directory.resolve("prothsync.json");

        // No auto-configuration: this checks real Boot logging without touching DB/Redis.
        try (var context = new SpringApplicationBuilder(LoggingApplication.class)
            .web(WebApplicationType.NONE)
            .run("--logging.file.name=" + file,
                "--logging.logback.rollingpolicy.file-name-pattern="
                    + directory.resolve("archive/prothsync.%d{yyyy-MM-dd}.%i.json.gz"),
                "--spring.main.banner-mode=off")) {
            LoggerFactory.getLogger("LOKI_SMOKE_TEST").error("Loki JSON verification",
                new IllegalStateException("outer failure", new RuntimeException("root cause")));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode event = null;
            for (String line : Files.readAllLines(file)) {
                JsonNode candidate = mapper.readTree(line);
                if ("LOKI_SMOKE_TEST".equals(candidate.path("logger_name").asText())) {
                    event = candidate;
                }
            }

            assertThat(event).isNotNull();
            assertThat(event.path("@timestamp").asText()).isNotBlank();
            assertThat(event.path("level").asText()).isEqualTo("ERROR");
            assertThat(event.path("service_name").asText()).isEqualTo("prothsync");
            assertThat(event.path("environment").asText()).isNotBlank();
            assertThat(event.path("stack_trace").asText())
                .contains("IllegalStateException: outer failure", "RuntimeException: root cause");
        } finally {
            // Release the file handle before JUnit removes the directory on Windows.
            ((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class LoggingApplication {
    }
}
