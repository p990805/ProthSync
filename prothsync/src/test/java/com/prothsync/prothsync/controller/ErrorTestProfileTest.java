package com.prothsync.prothsync.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorTestProfileTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withUserConfiguration(ErrorTestController.class);

    @Test
    void disabledByDefault() {
        runner.run(context -> assertThat(context).doesNotHaveBean(ErrorTestController.class));
    }

    @Test
    void enabledWithExplicitProfile() {
        runner.withInitializer(context -> context.getEnvironment().setActiveProfiles("error-test"))
            .run(context -> assertThat(context).hasSingleBean(ErrorTestController.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"prod", "production"})
    void disabledWhenProductionIsAlsoActive(String productionProfile) {
        runner.withInitializer(context -> context.getEnvironment()
                .setActiveProfiles("error-test", productionProfile))
            .run(context -> assertThat(context).doesNotHaveBean(ErrorTestController.class));
    }
}
