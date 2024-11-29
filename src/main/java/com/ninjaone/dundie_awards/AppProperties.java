package com.ninjaone.dundie_awards;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app")
public record AppProperties(
        @Min(1) @NotNull int batchSize,
        @Min(7000) @NotNull int messageProcessingIntervalMs,
        @NotNull RetrySaveActivityConfig retrySaveActivity,
        @NotNull boolean enableTestBehavior
) {
    public record RetrySaveActivityConfig(
            @Min(0) @NotNull int maxAttempts,
            @Min(0) @NotNull int backoffDelayMs
    ) {}
}
